package com.shuo.krpc.bootstrap;

import com.shuo.krpc.RpcApplication;

/**
 * Consumer Initialization Class
 * <p>
 * This class is responsible for initializing the service consumer. It sets up the RPC framework.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ConsumerBootstrap {
    public static void init() {
        // RPC Application initialization
        RpcApplication.init();
    }
}
