package com.shuo.krpc.server.tcp;

import com.shuo.krpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * TcpBufferHandlerWrapper
 * <p>
 * This class wraps a RecordParser around a buffer handler to enhance its functionality by using
 * the decorator pattern. It reads and processes both the header and body of protocol messages in
 * a structured manner.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    /**
     * Constructor for TcpBufferHandlerWrapper.
     *
     * @param bufferHandler The buffer handler to be wrapped and enhanced with record parsing
     *                      capability.
     */
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * The parser starts in fixed-size mode to read the header length, then switches to read the
     * body based on the size specified in the header. This ensures complete messages (header +
     * body) are read before being processed.
     *
     * @param bufferHandler The handler for processing complete messages.
     * @return The initialized RecordParser.
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // Create the parser to initially read the header length
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // Initial state to indicate that the message body size is not yet known
            int size = -1;
            // Buffer to store the complete message (header + body)
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    // Read the message body length from the header
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // Append the header information to the result buffer
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // Append the body information to the result buffer
                    resultBuffer.appendBuffer(buffer);
                    // The complete message is now assembled, process it
                    bufferHandler.handle(resultBuffer);
                    // Reset for the next message
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
