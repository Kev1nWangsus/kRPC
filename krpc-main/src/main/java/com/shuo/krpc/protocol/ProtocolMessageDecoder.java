package com.shuo.krpc.protocol;

import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.serializer.Serializer;
import com.shuo.krpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * Protocol message decoder.
 * <p>
 * This class is responsible for decoding protocol messages from byte buffers.
 * It reads the header and body of the protocol message and reconstructs the message object.
 *
 * <p>
 * The decoded buffer is expected to contain the following components in order:
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
public class ProtocolMessageDecoder {

    /**
     * Decode the protocol message from a buffer.
     *
     * @param buffer The buffer containing the encoded protocol message.
     * @return The decoded ProtocolMessage object.
     * @throws IOException If an I/O error occurs during decoding.
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // Read the header from the buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        // Validate magic byte
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("Invalid message magic byte");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // Handle sticky packet problem by only reading the specified length of data
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());

        // Deserialize the message body
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("Serializer does not exist");
        }

        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum
                .getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("Message type does not exist");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        return switch (messageTypeEnum) {
            case REQUEST -> {
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                yield new ProtocolMessage<>(header, request);
            }
            case RESPONSE -> {
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                yield new ProtocolMessage<>(header, response);
            }
            default -> throw new RuntimeException("Unsupported message type");
        };
    }
}