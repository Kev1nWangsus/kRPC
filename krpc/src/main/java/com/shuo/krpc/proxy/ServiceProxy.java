package com.shuo.krpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.serializer.JdkSerializer;
import com.shuo.krpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Service Proxy
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * invoke proxy
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // assign serializer
        Serializer serializer = new JdkSerializer();

        // construct request
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // todo: add register center and service discovery
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();

                // deserialize response
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
