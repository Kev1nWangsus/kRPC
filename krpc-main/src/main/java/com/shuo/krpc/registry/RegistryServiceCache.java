package com.shuo.krpc.registry;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {

    /**
     * Store cached services
     */
    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    void clearCache() {
        this.serviceCache = null;
    }
}
