package com.shuo.krpc.serializer;

import java.io.IOException;

/**
 * Serializer interface
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface Serializer {

    /**
     * Serialization function
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * Deserialization function
     *
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
