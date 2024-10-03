package com.shuo.demo.common.service;

import com.shuo.demo.common.model.User;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface UserService {

    /**
     * Get user
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * mock
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
