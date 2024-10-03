package com.shuo.provider;

import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.server.HttpServer;
import com.shuo.krpc.server.VertxHttpServer;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class SimpleProviderExample {
    public static void main(String[] args) {
        // register a service
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
