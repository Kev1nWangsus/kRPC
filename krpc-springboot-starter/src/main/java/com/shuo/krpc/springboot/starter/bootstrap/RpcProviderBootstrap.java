package com.shuo.krpc.springboot.starter.bootstrap;

import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.model.ServiceMetaInfo;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * RPC Service Provider Bootstrap
 * <p>
 * This class is responsible for initializing and registering RPC services after bean
 * initialization. It handles local and registry-based service registration for each annotated
 * service provider.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * Executes after bean initialization to register the service.
     * <p>
     * This method is called after a bean is initialized, and it checks if the bean is annotated
     * with {@link RpcService}. If it is, the service is registered locally and with the registry
     * center.
     *
     * @param bean     The bean instance created by Spring.
     * @param beanName The name of the bean.
     * @return The processed bean instance.
     * @throws BeansException If any error occurs during bean post-processing.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // Register the service
            // 1. Get basic service information
            Class<?> interfaceClass = rpcService.interfaceClass();
            // Handle default value
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 2. Register the service
            // Local registration
            LocalRegistry.register(serviceName, beanClass);

            // Global configuration
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // Register the service with the registry center
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " service registration failed", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}