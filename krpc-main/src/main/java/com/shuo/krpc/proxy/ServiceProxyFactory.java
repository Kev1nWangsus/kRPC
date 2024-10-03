package com.shuo.krpc.proxy;

import java.lang.reflect.Proxy;

/**
 * Service Proxy Factory (for service proxy creation)
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceProxyFactory {

    /**
     * get proxy based on service class
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}