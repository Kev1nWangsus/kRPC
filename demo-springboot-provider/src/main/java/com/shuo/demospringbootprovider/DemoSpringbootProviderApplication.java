package com.shuo.demospringbootprovider;

import com.shuo.krpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class DemoSpringbootProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbootProviderApplication.class, args);
    }

}
