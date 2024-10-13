package com.shuo.demospringbootconsumer;

import com.shuo.krpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class DemoSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbootConsumerApplication.class, args);
    }

}