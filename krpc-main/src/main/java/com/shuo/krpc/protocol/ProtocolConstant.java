package com.shuo.krpc.protocol;

/**
 * Protocol message constant
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface ProtocolConstant {

    /**
     * Message header length
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * Protocol magic number for ensuring security
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * Protocol version number
     */
    byte PROTOCOL_VERSION = 0x1;
}