package com.shuo.krpc.springboot.starter.bootstrap;

import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.server.tcp.VertxTcpServer;
import com.shuo.krpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * RPC Framework Initialization Bootstrap
 * <p>
 * This class is responsible for initializing the RPC framework when the Spring application starts.
 * It implements the {@link ImportBeanDefinitionRegistrar} to register bean definitions required
 * for RPC.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Initializes the RPC framework during Spring initialization.
     * <p>
     * This method is called by Spring to register bean definitions. It checks if the RPC server
     * needs to be started based on the {@link EnableRpc} annotation attributes and performs the
     * necessary initialization of the RPC framework, including starting the server if required.
     *
     * @param importingClassMetadata Metadata of the importing class.
     * @param registry               The BeanDefinitionRegistry used to register bean definitions.
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        // Get the attribute value of the EnableRpc annotation
        boolean needServer =
                (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // Initialize the RPC framework (configuration and registry)
        RpcApplication.init();

        // Global configuration
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // Start the server if needed
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("Server is not started");
        }
    }
}
