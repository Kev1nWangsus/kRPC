package com.shuo.provider;

import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.RpcApplication;
import com.shuo.krpc.registry.LocalRegistry;
import com.shuo.krpc.server.HttpServer;
import com.shuo.krpc.server.VertxHttpServer;

/**
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ProviderExample {
    public static void main(String[] args) {
        // RPC framework initialization
        RpcApplication.init();

        // Register services
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // Start the web server
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
