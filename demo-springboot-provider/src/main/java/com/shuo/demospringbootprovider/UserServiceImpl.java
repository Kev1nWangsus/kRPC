package com.shuo.demospringbootprovider;

import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("Username: " + user.getName());
        return user;
    }
}