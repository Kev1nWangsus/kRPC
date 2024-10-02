package com.shuo.krpc.server;

import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.serializer.JdkSerializer;
import com.shuo.krpc.serializer.Serializer;
import io.vertx.core.Expectation;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // assign serializer
        final Serializer serializer = new JdkSerializer();

        System.out.println("Received request: " + request.method() + " " + request.uri());

        // async http
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // construct response object
            RpcResponse rpcResponse = new RpcResponse();

            // immediately return if request is null
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpc request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // access service implementation using reflection
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(),
                        rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * response
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // serialization
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
