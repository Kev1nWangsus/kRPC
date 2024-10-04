package com.shuo.demo.consumer;

import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.config.RpcConfig;
import com.shuo.krpc.proxy.ServiceProxyFactory;
import com.shuo.krpc.utils.ConfigUtils;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);

        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);

        long test_number = userService.getNumber();
        System.out.println(test_number);
    }



}
