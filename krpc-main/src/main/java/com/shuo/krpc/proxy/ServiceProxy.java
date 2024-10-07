package com.shuo.krpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.constant.RpcConstant;
import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.model.ServiceMetaInfo;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.serializer.Serializer;
import com.shuo.krpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Service Proxy
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * Invoke proxy
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // assign serializer
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // construct request
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry =
                RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("No service address found!");
        }

        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // todo: add register center and service discovery
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
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
