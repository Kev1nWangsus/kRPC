package com.shuo.provider;

import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.server.tcp.VertxTcpServer;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ProviderExample {
    public static void main(String[] args) {

        // RPC framework initialization
        RpcApplication.init();

        // Register services
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Start the Vertx server
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
