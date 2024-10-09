package com.shuo.krpc.registry;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceCache {

    /**
     * Store cached services
     */
    private final Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * Write new cache for a specific service key
     *
     * @param serviceKey The key representing the service
     * @param newServiceCache The list of ServiceMetaInfo to cache
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        serviceCache.put(serviceKey, newServiceCache);
    }

    /**
     * Read cache for a specific service key
     *
     * @param serviceKey The key representing the service
     * @return The list of ServiceMetaInfo cached, or null if not found
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return serviceCache.get(serviceKey);
    }

    /**
     * Clear the entire cache
     */
    void clearCache() {
        serviceCache.clear();
    }

    /**
     * Clear the cache for a specific service key
     *
     * @param serviceKey The key representing the service to be removed from the cache
     */
    void clearCache(String serviceKey) {
        serviceCache.remove(serviceKey);
    }

    /**
     * Update the cache for a specific service key
     *
     * @param serviceKey The key representing the service
     * @param updatedServiceMetaInfo The updated ServiceMetaInfo to cache
     */
    void updateCache(String serviceKey, ServiceMetaInfo updatedServiceMetaInfo) {
        if (serviceCache.containsKey(serviceKey)) {
            List<ServiceMetaInfo> serviceMetaInfos = serviceCache.get(serviceKey);
            if (serviceMetaInfos != null) {
                serviceMetaInfos.removeIf(metaInfo -> metaInfo.getServiceNodeKey().equals(updatedServiceMetaInfo.getServiceNodeKey()));
                serviceMetaInfos.add(updatedServiceMetaInfo);
            }
        }
    }
}
