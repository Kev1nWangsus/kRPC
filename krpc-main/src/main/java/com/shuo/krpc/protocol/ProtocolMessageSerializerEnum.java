package com.shuo.krpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration for protocol message serializers.
 * <p>
 * This enum represents different serializers that can be used for protocol messages.
 * It provides methods to get the list of available serializers and to retrieve enum instances by key or value.
 *
 * <ul>
 *     <li>JDK - Represents the JDK serializer with a key of 0.</li>
 *     <li>JSON - Represents the JSON serializer with a key of 1.</li>
 *     <li>KRYO - Represents the Kryo serializer with a key of 2.</li>
 *     <li>HESSIAN - Represents the Hessian serializer with a key of 3.</li>
 * </ul>
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the list of all serializer values.
     *
     * @return A list of strings representing the names of all serializers.
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * Get the enum instance by key.
     *
     * @param key The integer key of the desired serializer.
     * @return The corresponding ProtocolMessageSerializerEnum, or null if no matching key is found.
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * Get the enum instance by value.
     *
     * @param value The string value of the desired serializer.
     * @return The corresponding ProtocolMessageSerializerEnum, or null if no matching value is found.
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}