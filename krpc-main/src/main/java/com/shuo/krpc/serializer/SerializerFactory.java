package com.shuo.krpc.serializer;

import com.shuo.krpc.spi.SpiLoader;

/**
 * SerializerFactory for serializer obtainment
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * Default serializer set to JDK implementation
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * Load serializer from factory
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
