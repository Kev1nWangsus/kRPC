package com.shuo.demospringbootconsumer;

import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("shuo");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}