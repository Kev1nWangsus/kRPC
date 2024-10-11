package com.shuo.demo.consumer;

import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.bootstrap.ConsumerBootstrap;
import com.shuo.krpc.proxy.ServiceProxyFactory;

public class ConsumerBootstrapExample {
    public static void main(String[] args) {
        // Consumer initialization
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("shuo");

        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
