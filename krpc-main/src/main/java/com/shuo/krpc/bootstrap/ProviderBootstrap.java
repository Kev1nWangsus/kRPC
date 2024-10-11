package com.shuo.krpc.bootstrap;

import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import com.shuo.krpc.model.ServiceRegistrationInfo;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * Provider Initialization Class
 * <p>
 * This class is responsible for initializing the service provider. It sets up the RPC framework,
 * registers services locally and with the registry center, and starts the server to handle
 * incoming requests.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ProviderBootstrap {

    /**
     * Initialize the service provider.
     * <p>
     * This method performs the initialization of the RPC framework, including setting up
     * configurations, registering services both locally and with the registry, and starting the
     * server.
     *
     * @param serviceRegistrationInfoList The list of services to be registered.
     */
    public static void init(List<ServiceRegistrationInfo<?>> serviceRegistrationInfoList) {
        // Initialize RPC framework (configuration and registry center)
        RpcApplication.init();
        // Global configuration
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // Register services
        for (ServiceRegistrationInfo<?> serviceRegistrationInfo : serviceRegistrationInfoList) {
            String serviceName = serviceRegistrationInfo.getServiceName();
            // Local registration
            LocalRegistry.register(serviceName, serviceRegistrationInfo.getImplClass());

            // Register service with the registry center
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("Service registration failed for " + serviceName, e);
            }
        }

        // Start the server
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
