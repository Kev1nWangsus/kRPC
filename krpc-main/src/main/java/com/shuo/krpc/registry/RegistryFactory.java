package com.shuo.krpc.registry;

import com.shuo.krpc.spi.SpiLoader;

/**
 * RegistryFactory for registry instantiation
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * Default registry set to etcd implementation
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * Load registry from factory
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
