package com.shuo.krpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Redis registry
 * <p>
 * Manages service registration and discovery using Redis. It provides functionality to register,
 * unregister, discover services, handle heartbeat checks, and manage watch mechanisms for service
 * nodes.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class RedisRegistry implements Registry {

    private RedissonClient redissonClient;

    /**
     * A set of all locally registered nodes' keys (for lease renewal)
     */
    private final Set<String> localRegisteredNodeKeySet = new ConcurrentHashSet<>();

    /**
     * Cached services in registry
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * A map to store the registration time of each service node
     */
    private final Map<String, Instant> registrationTimeMap = new ConcurrentHashMap<>();

    /**
     * Root node
     */
    private static final String REDIS_ROOT_PATH = "rpc:";

    /**
     * Lease duration in seconds
     */
    private static final int LEASE_DURATION_SECONDS = 30;

    /**
     * Heartbeat schedule cron expression
     */
    private static final String HEARTBEAT_SCHEDULE = "*/10 * * * * *";

    @Override
    public void init(RegistryConfig registryConfig) {
        Config config = new Config();
        config.useSingleServer().setAddress(registryConfig.getAddress());
        redissonClient = Redisson.create(config);

        // Start heartbeat scheduler
        sendHeartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) {
        try {
            String registerKey = REDIS_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
            RBucket<String> bucket = redissonClient.getBucket(registerKey);
            bucket.set(JSONUtil.toJsonStr(serviceMetaInfo),
                    Duration.ofSeconds(LEASE_DURATION_SECONDS));

            localRegisteredNodeKeySet.add(registerKey);
            registrationTimeMap.put(registerKey, Instant.now());
            log.info("Service registered: {}, at time: {}", serviceMetaInfo.getServiceNodeKey(),
                    registrationTimeMap.get(registerKey));
        } catch (Exception e) {
            log.error("Failed to register service: {}", serviceMetaInfo.getServiceNodeKey(), e);
            throw new RuntimeException("Error registering service", e);
        }
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String unregisterKey = REDIS_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        try {
            redissonClient.getBucket(unregisterKey).delete();
            localRegisteredNodeKeySet.remove(unregisterKey);
            registrationTimeMap.remove(unregisterKey);
            log.info("Service unregistered: {}", serviceMetaInfo.getServiceNodeKey());
            registryServiceCache.clearCache(unregisterKey);
        } catch (Exception e) {
            log.error("Failed to unregister service: {}", serviceMetaInfo.getServiceNodeKey(), e);
            throw new RuntimeException("Error unregistering service", e);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // Try to fetch from cache
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {
            RMap<String, String> serviceMap = redissonClient.getMap(REDIS_ROOT_PATH + serviceKey);
            List<ServiceMetaInfo> serviceMetaInfoList = serviceMap.readAllValues().stream()
                    .map(value -> JSONUtil.toBean(value, ServiceMetaInfo.class))
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            log.error("Failed to get service list for key: {}", serviceKey, e);
            return null;
        }
    }

    @Override
    public void destroy() {
        log.info("Deactivate current node");
        // Deactivate node
        for (String key : localRegisteredNodeKeySet) {
            try {
                redissonClient.getBucket(key).delete();
            } catch (Exception e) {
                log.error("Failed to deactivate key: {}", key, e);
                throw new RuntimeException(key + " failed to deactivate", e);
            }
        }

        // Release resources
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }

    @Override
    public void sendHeartBeat() {
        // Renew every 10 seconds
        CronUtil.schedule(HEARTBEAT_SCHEDULE, (Task) () -> {
            for (String key : localRegisteredNodeKeySet) {
                try {
                    RBucket<String> bucket = redissonClient.getBucket(key);
                    if (!bucket.isExists()) {
                        continue;
                    }
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(bucket.get(),
                            ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    log.error("Failed to renew lease for key: {}", key, e);
                    throw new RuntimeException(key + " failed to renew", e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        // Redis does not natively support watch mechanisms like etcd
    }
}
