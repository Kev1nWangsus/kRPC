package com.shuo.krpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ZooKeeper registry
 * <p>
 * Maintains a registry of service metadata using ZooKeeper to manage and discover services dynamically.
 * This registry includes features for registering, unregistering, discovering services, and handling service instance heartbeats.
 * It uses CuratorFramework for client interactions with ZooKeeper and manages a local cache for registered services to optimize lookup operations.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class ZooKeeperRegistry implements Registry {

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * A set of all locally registered nodes' keys (for lease renewal)
     */
    private final Set<String> localRegisterNodeKeySet = new ConcurrentHashSet<>();

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
    private static final String ZOOKEEPER_ROOT_PATH = "/rpc/zk";


    @Override
    public void init(RegistryConfig registryConfig) {
        // Instantiate client with ZooKeeper connection parameters
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        // Instantiate service discovery using ZooKeeper
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZOOKEEPER_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // Register with ZooKeeper
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // Add node information to local cache
        String registerKey = ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Remove from local cache
        String registerKey = ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // First attempt to get service from cache
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {
            // Query service information
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery
                    .queryForInstances(serviceKey);

            // Parse service information
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            // Write to service cache
            registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain service list", e);
        }
    }

    @Override
    public void sendHeartBeat() {
        // No heartbeat mechanism is needed, as temporary nodes are established; if the server fails
        // , the temporary nodes will be lost automatically.
    }

    /**
     * Listen (on the consumer side)
     *
     * @param serviceNodeKey Service node key
     */
    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZOOKEEPER_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges((oldNode, node) -> registryServiceCache.clearCache())
                            .build()
            );
        }
    }

    @Override
    public void destroy() {
        log.info("Deactivate current node");

        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + " failed to deactivate");
            }
        }

        // Release resources
        if (client != null) {
            client.close();
        }
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" +
                serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}