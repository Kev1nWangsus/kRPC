package com.shuo.krpc.protocol;

import lombok.Getter;

/**
 * ProtocolMessageTypeEnum is an enumeration that represents different types of protocol messages.
 * It contains the following message types:
 *
 * <ul>
 *     <li>REQUEST - Represents a request message with a key of 0.</li>
 *     <li>RESPONSE - Represents a response message with a key of 1.</li>
 *     <li>HEART_BEAT - Represents a heartbeat message with a key of 2.</li>
 *     <li>OTHERS - Represents other types of messages with a key of 3.</li>
 * </ul>
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * Get the corresponding enum instance by key.
     *
     * @param key The integer value of the desired message type.
     * @return The corresponding ProtocolMessageTypeEnum, or null if no matching value is found.
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
