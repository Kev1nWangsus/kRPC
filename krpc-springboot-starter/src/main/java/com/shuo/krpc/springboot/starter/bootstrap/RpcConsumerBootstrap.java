package com.shuo.krpc.springboot.starter.bootstrap;

import com.shuo.krpc.proxy.ServiceProxyFactory;
import com.shuo.krpc.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC Consumer Bootstrap
 * <p>
 * This class is responsible for initializing and injecting RPC services into Spring beans after
 * their initialization. It implements the {@link BeanPostProcessor} interface to create proxies
 * for fields annotated with {@link RpcReference}.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Executes after bean initialization to inject services.
     * <p>
     * This method is called after a bean is initialized. It checks if any fields in the bean are
     * annotated with {@link RpcReference}. If such fields are found, it creates a proxy for the
     * field's interface and injects it.
     *
     * @param bean     The bean instance created by Spring.
     * @param beanName The name of the bean.
     * @return The processed bean instance.
     * @throws BeansException If any error occurs during bean post-processing.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // Iterate through all fields of the object
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // Create a proxy object for the field
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject proxy object for field", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
