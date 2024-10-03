package com.shuo.krpc.server;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP server
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class VertxHttpServer implements HttpServer {

    /**
     * start the server
     *
     * @param port
     */
    @Override
    public void doStart(int port) {
        // create vertx instance
        Vertx vertx = Vertx.vertx();

        // create http server
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server.requestHandler(new HttpServerHandler());

        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}