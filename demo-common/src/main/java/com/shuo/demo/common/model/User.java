package com.shuo.demo.common.model;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class User implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
