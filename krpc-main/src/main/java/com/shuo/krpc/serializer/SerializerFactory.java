package com.shuo.krpc.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * SerializerFactory for serializer obtainment
 */
public class SerializerFactory {
    /**
     * Serializer map for singleton
     */
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {{
        put(SerializerKeys.JDK, new JdkSerializer());
        put(SerializerKeys.JSON, new JsonSerializer());
        put(SerializerKeys.KRYO, new KryoSerializer());
        put(SerializerKeys.HESSIAN, new HessianSerializer());
    }};

    /**
     * Default serializer set to JDK implementation
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    /**
     * Getter
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }
}
