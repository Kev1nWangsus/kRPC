package com.shuo.demo.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.serializer.JdkSerializer;
import com.shuo.krpc.serializer.Serializer;

import java.io.IOException;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class UserServiceProxy implements UserService {

    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;

            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
