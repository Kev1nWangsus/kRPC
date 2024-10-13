package com.shuo.demospringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoServiceImplTest {

    @Resource
    private DemoServiceImpl demoService;

    @Test
    void test1() {
        demoService.test();
    }
}