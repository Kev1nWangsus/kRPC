package com.shuo.krpc.server.tcp;

import com.shuo.krpc.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * Vertx TCP Server
 * <p>
 * This class represents a TCP server implemented using Vert.x. It starts a TCP server and
 * listens on the specified port. Incoming connections are handled by the {@link TcpServerHandler}.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class VertxTcpServer implements Server {

    /**
     * Starts the TCP server on the specified port.
     *
     * @param port The port number on which the TCP server should listen.
     */
    @Override
    public void doStart(int port) {
        // Create a Vert.x instance
        Vertx vertx = Vertx.vertx();

        // Create a TCP server
        NetServer server = vertx.createNetServer();

        // Set the connection handler
        server.connectHandler(new TcpServerHandler());

        // Start the TCP server and listen on the specified port
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });
    }
}