package com.shuo.krpc.protocol;

import lombok.Getter;

/**
 * ProtocolMessageStatusEnum is an enumeration that represents the status of a protocol message.
 * It contains the following status types:
 *
 * <ul>
 *     <li>OK - Represents a successful operation with a value of 20.</li>
 *     <li>BAD_REQUEST - Represents a bad request with a value of 40.</li>
 *     <li>BAD_RESPONSE - Represents a bad response with a value of 50.</li>
 * </ul>
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Get the corresponding enum instance by value.
     *
     * @param value The integer value of the desired status.
     * @return The corresponding ProtocolMessageStatusEnum, or null if no matching value is found.
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }
}