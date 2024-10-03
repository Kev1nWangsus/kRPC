package com.shuo.provider;


import com.shuo.demo.common.model.User;
import com.shuo.demo.common.service.UserService;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("Username: " + user.getName());
        return user;
    }
}
