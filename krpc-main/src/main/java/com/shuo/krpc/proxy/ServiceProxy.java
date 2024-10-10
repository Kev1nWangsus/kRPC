package com.shuo.krpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.constant.RpcConstant;
import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;
import com.shuo.krpc.model.ServiceMetaInfo;
import com.shuo.krpc.registry.Registry;
import com.shuo.krpc.registry.RegistryFactory;
import com.shuo.krpc.server.tcp.VertxTcpClient;
import com.shuo.krpc.loadbalancer.LoadBalancer;
import com.shuo.krpc.loadbalancer.LoadBalancerFactory;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Proxy
 * <p>
 * This class acts as a proxy for services, enabling dynamic invocation using JDK dynamic proxies.
 * It facilitates sending requests to remote services and receiving responses, utilizing the
 * specified serializer and making TCP connections to service providers discovered via the registry.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * Invokes the proxy method. It creates the request, serializes it, sends it to the
     * appropriate services, and deserializes the response.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the method instance corresponding to the interface method invoked on the
     *               proxy instance
     * @param args   the arguments passed when the method was invoked
     * @return the result of the remote service method invocation
     * @throws Throwable if an exception occurs during method invocation
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Construct the request
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        // Retrieve the service provider address from the registry
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry =
                RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList =
                registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("No service address available");
        }

        // Select service node based on load balancer
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // Use method name as load balancer parameter
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams,
                serviceMetaInfoList);

        // Send TCP request
        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
        return rpcResponse.getData();
    }
}
