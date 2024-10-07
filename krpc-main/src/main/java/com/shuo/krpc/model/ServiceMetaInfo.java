package com.shuo.krpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * Service meta info (registry information)
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
public class ServiceMetaInfo {

    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceHost;

    private String servicePort;

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
        return String.format("%s/%s:%s", serviceName, serviceHost, servicePort);
    }

    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
