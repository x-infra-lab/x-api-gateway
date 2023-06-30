package io.github.xinfra.lab.gateway;

import io.github.xinfra.lab.gateway.bootstrap.GatewayServerBootstrap;
import reactor.netty.DisposableServer;


public class BaseTest {

    /**
     * use default config
     *
     * @param port
     * @return
     */
    public DisposableServer startGatewayServer(int port) {
        DisposableServer server = new GatewayServerBootstrap()
                .port(port)
                .start();
        return server;
    }


    /**
     * use
     *
     * @param port
     * @param configPath
     * @return
     */
    public DisposableServer startGatewayServer(int port,
                                               String configPath) {

        DisposableServer server = new GatewayServerBootstrap()
                .port(port)
                .configPath(configPath)
                .start();
        return server;
    }

}
