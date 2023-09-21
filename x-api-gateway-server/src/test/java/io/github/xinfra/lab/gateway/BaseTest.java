package io.github.xinfra.lab.gateway;

import io.github.xinfra.lab.gateway.bootstrap.GatewayServerBootstrap;
import io.github.xinfra.lab.gateway.route.RouteLocator;


public class BaseTest {

    /**
     * use default config
     *
     * @param port
     * @return
     */
    public GatewayServerBootstrap startGatewayServer(int port) {
        return new GatewayServerBootstrap()
                .port(port)
                .start();
    }


    /**
     * use custom config path
     *
     * @param port
     * @param configPath
     * @return
     */
    public GatewayServerBootstrap startGatewayServer(int port,
                                                     String configPath) {

        return new GatewayServerBootstrap()
                .port(port)
                .configPath(configPath)
                .start();
    }

    /**
     * use custom routeLocator
     *
     * @param port
     * @param routeLocator
     * @return
     */
    public GatewayServerBootstrap startGatewayServer(int port,
                                                     RouteLocator routeLocator) {

        return new GatewayServerBootstrap()
                .port(port)
                .routeLocator(routeLocator)
                .start();
    }
}
