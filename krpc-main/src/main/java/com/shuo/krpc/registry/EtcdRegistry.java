package com.shuo.krpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

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
    private final Set<String> localRegisteredNodeKeySey = new HashSet<>();

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

        localRegisteredNodeKeySey.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String unRegisterKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(unRegisterKey, StandardCharsets.UTF_8));

        localRegisteredNodeKeySey.remove(unRegisterKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service list", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("Deactivate current node");
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
           for (String key: localRegisteredNodeKeySey) {
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
}
