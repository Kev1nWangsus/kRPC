package com.shuo.krpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Etcd registry
 * <p>
 * Manages service registration and discovery using etcd. It provides functionality to register,
 * unregister, discover services, handle heartbeat checks, and manage watch mechanisms for service
 * nodes
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * A set of all locally registered nodes' keys (for lease renewal)
     */
    private final Set<String> localRegisteredNodeKeySet = ConcurrentHashMap.newKeySet();

    /**
     * Cached services in registry
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * A set of all watching keys
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * Root node
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

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
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();

        // Start heartbeat scheduler
        sendHeartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // Create lease and kv clients
        Lease leaseClient = client.getLeaseClient();

        // Create a lease
        long leaseId = leaseClient.grant(LEASE_DURATION_SECONDS).get().getID();

        // Create service info
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = getByteSequence(registerKey);
        ByteSequence value = getByteSequence(JSONUtil.toJsonStr(serviceMetaInfo));

        // Associate service information with lease
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        localRegisteredNodeKeySet.add(registerKey);
        log.info("Service registered: {}", serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String unregisterKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        try {
            kvClient.delete(getByteSequence(unregisterKey)).get();
            localRegisteredNodeKeySet.remove(unregisterKey);
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

        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(getByteSequence(searchPrefix), getOption)
                    .get()
                    .getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // Watch key
                        watch(key);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
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
                kvClient.delete(getByteSequence(key)).get();
            } catch (Exception e) {
                log.error("Failed to deactivate key: {}", key, e);
                throw new RuntimeException(key + " failed to deactivate", e);
            }
        }

        // Release resources
        try {
            if (kvClient != null) {
                kvClient.close();
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public void sendHeartBeat() {
        // Renew every 10 seconds
        CronUtil.schedule(HEARTBEAT_SCHEDULE, (Task) () -> {
            for (String key : localRegisteredNodeKeySet) {
                try {
                    List<KeyValue> keyValues = kvClient
                            .get(getByteSequence(key))
                            .get()
                            .getKvs();
                    if (CollUtil.isEmpty(keyValues)) {
                        continue;
                    }

                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
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
        Watch watchClient = client.getWatchClient();
        // Start watching
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(getByteSequence(serviceNodeKey), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            // Remove cache for specific service key
                            registryServiceCache.clearCache(serviceNodeKey);
                            break;
                        case PUT:
                            String updatedValue = event.getKeyValue().getValue()
                                    .toString(StandardCharsets.UTF_8);
                            ServiceMetaInfo updatedMetaInfo = JSONUtil.toBean(updatedValue,
                                    ServiceMetaInfo.class);
                            registryServiceCache.updateCache(serviceNodeKey, updatedMetaInfo);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private ByteSequence getByteSequence(String str) {
        return ByteSequence.from(str, StandardCharsets.UTF_8);
    }
}
