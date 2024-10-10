package com.shuo.krpc.server.tcp;

import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.protocol.*;
import com.shuo.krpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TcpServerHandler
 * <p>
 * TcpServerHandler processes the incoming request by decoding it, invoking the appropriate service
 * method, and sending back the response. It utilizes the ProtocolMessageDecoder and
 * ProtocolMessageEncoder to handle encoding and decoding of protocol messages.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        // Handle the connection

        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // Receive the request and decode it
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage =
                        (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("Protocol message decoding error");
            }

            // Process the request
            RpcRequest rpcRequest = protocolMessage.getBody();

            // Construct the response object
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // Get the service implementation class and invoke the method via reflection
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(),
                        rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(),
                        rpcRequest.getArgs());
                // Package the return result
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // Send the response by encoding it
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header,
                    rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("Protocol message encoding error");
            }
        });
        netSocket.handler(bufferHandlerWrapper);
    }
}
