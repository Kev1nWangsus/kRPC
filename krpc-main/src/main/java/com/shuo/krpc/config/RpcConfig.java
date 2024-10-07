package com.shuo.krpc.config;

import com.shuo.krpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC Configuration
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
public class RpcConfig {

    /**
     * Framework name
     */
    private String name = "krpc";

    /**
     * Version number
     */
    private String version = "1.0";

    /**
     * Server hostname
     */
    private String serverHost = "localhost";

    /**
     * Server port number
     */
    private Integer serverPort = 8080;

    /**
     * Serializer
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * Developer mock mode
     */
    private boolean mock = false;

    /**
     * Registry config
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
