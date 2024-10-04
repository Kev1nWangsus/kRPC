package com.shuo.krpc.proxy;

import com.shuo.krpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * Service Proxy Factory (for service proxy creation)
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceProxyFactory {

    /**
     * Get proxy based on service class
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

    /**
     * Get mock proxy based on service class
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}