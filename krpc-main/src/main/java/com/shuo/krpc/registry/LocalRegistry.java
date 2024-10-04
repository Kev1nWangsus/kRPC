package com.shuo.krpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local registry
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class LocalRegistry {

    /**
     * Registration information storage
     */
    private static final Map<String, Class<?>> registrationMap = new ConcurrentHashMap<>();

    /**
     * Register service
     *
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        registrationMap.put(serviceName, implClass);
    }

    /**
     * Access service based on name
     *
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return registrationMap.get(serviceName);
    }

    /**
     * Remove service based on name
     *
     * @param serviceName
     */
    public static void remove(String serviceName) {
        registrationMap.remove(serviceName);
    }
}
