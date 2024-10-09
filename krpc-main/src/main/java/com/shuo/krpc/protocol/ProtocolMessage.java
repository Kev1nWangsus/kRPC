package com.shuo.krpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Protocol message for custom RPC protocol
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * Message header
     */
    private Header header;

    /**
     * Message body (request or response object)
     */
    private T body;

    /**
     * Protocol message header
     */
    @Data
    public static class Header {

        /**
         * Magic number for ensuring security
         */
        private byte magic;

        /**
         * Protocol version
         */
        private byte version;

        /**
         * Serializer type
         */
        private byte serializer;

        /**
         * Message type (request/response)
         */
        private byte type;

        /**
         * Status of the message
         */
        private byte status;

        /**
         * Request ID
         */
        private long requestId;

        /**
         * Length of the message body
         */
        private int bodyLength;
    }
}

