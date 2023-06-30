package io.github.xinfra.lab.gateway.route;

import io.github.xinfra.lab.gateway.config.GatewayConfig;
import reactor.core.publisher.Flux;


public class GatewayConfigRouteDefinitionLocator implements RouteDefinitionLocator {

    private GatewayConfig gatewayConfig;

    public GatewayConfigRouteDefinitionLocator(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(gatewayConfig.getRoutes());
    }

}
