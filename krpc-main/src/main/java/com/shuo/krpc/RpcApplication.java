package com.shuo.krpc;

import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.constant.RpcConstant;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC Application
 * Double-checked locking singleton with lazy initialization
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * Framework initialization with custom configuration
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // registry initialization
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * Initialization
     */
    public static void init() {
        RpcConfig newRpcConfig;

        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class,
                    RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * Get RPC configuration
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
