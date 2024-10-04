package com.shuo.krpc.server;

/**
 * HTTPServer interface
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface HttpServer {

    /**
     * Start the server
     *
     * @param port
     */
    void doStart(int port);
}