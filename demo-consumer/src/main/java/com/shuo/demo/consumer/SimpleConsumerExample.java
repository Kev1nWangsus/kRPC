package com.shuo.demo.consumer;

import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.proxy.ServiceProxyFactory;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class SimpleConsumerExample {
    public static void main(String[] args) {

        // static proxy
        // UserService userService = new UserServiceProxy();

        // dynamic proxy
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
