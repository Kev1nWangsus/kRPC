package com.shuo.krpc.protocol;

import com.shuo.krpc.serializer.Serializer;
import com.shuo.krpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * Protocol message encoder.
 * <p>
 * This class is responsible for encoding protocol messages into byte buffers.
 * It encodes the header and body of the protocol message into a buffer that can be transmitted.
 *
 * <p>
 * The encoded buffer contains the following components in order:
 * <ul>
 *     <li>Magic byte</li>
 *     <li>Version byte</li>
 *     <li>Serializer type byte</li>
 *     <li>Message type byte</li>
 *     <li>Status byte</li>
 *     <li>Request ID (long)</li>
 *     <li>Body length (int)</li>
 *     <li>Body bytes</li>
 * </ul>
 * </p>
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ProtocolMessageEncoder {

    /**
     * Encode the protocol message into a buffer.
     *
     * @param protocolMessage The protocol message to encode.
     * @return The encoded buffer containing the serialized protocol message.
     * @throws IOException If an I/O error occurs during encoding.
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();

        // Sequentially write bytes to the buffer
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        // Get the serializer
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());

        if (serializerEnum == null) {
            throw new RuntimeException("Serialization protocol does not exist");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());

        // Write body length and data
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
