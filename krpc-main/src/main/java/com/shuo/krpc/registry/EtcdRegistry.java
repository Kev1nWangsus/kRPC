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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Etcd registry
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * A set of all locally registered nodes' keys (for lease renewal)
     */
    private final Set<String> localRegisteredNodeKeySet = new HashSet<>();

    /**
     * Cached services
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

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();

        // start heartBeat scheduler
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // create lease and kv clients
        Lease leaseClient = client.getLeaseClient();

        // create a 30s lease
        long leaseId = leaseClient.grant(30).get().getID();

        // create service info
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // associate service information with lease
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        localRegisteredNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String unRegisterKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(unRegisterKey, StandardCharsets.UTF_8));

        localRegisteredNodeKeySet.remove(unRegisterKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // try to fetch from cache
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        // no cache
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // watch key
                        watch(key);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service list", e);
        }
    }

    @Override
    public void destroy() {
        // deactivate node
        for (String key: localRegisteredNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "failed to deactivate");
            }
        }

        // release resources
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // renew every 10 seconds
        CronUtil.schedule("*/10 * * * * * ", (Task) () -> {
           for (String key: localRegisteredNodeKeySet) {
               try {
                   List<KeyValue> keyValues = kvClient
                           .get(ByteSequence.from(key, StandardCharsets.UTF_8))
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
                   throw new RuntimeException(key + "failed to renew", e);
               }
           }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // start watching
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event: response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            // remove cache
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
