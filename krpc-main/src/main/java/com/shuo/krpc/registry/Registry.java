package com.shuo.krpc.registry;

import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * Registry interface
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface Registry {

    /**
     * Registry initialization
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * Register a service (service provider side)
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * Unregister a service (service provider side)
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * Service discovery
     * @param serviceKey
     * @return a list of all nodes of target service
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * Remove current registry node
     */
    void destroy();

    /**
     * Heartbeat detection
     */
    void heartBeat();

    /**
     * Watch a service node (consumer side)
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
