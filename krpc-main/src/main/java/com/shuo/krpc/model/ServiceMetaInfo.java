package com.shuo.krpc.model;

/**
 * Service meta info (registry information)
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceMetaInfo {

    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceAddress;

    /**
     * Service key getter
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * Service node key getter
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s", serviceName, serviceAddress);
    }
}
