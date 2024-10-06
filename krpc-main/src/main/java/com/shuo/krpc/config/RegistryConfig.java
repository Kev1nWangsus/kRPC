package com.shuo.krpc.config;

import lombok.Data;

/**
 * Registry Configuration
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
public class RegistryConfig {
    /**
     * Registry type
     */
    private String registry = "etcd";

    /**
     * Registry address
     */
    private String address = "http://localhost:2380";

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * timeout
     */
    private Long timeout = 10000L;
}
