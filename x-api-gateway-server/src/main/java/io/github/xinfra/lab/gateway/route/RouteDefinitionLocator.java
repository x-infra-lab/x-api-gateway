package io.github.xinfra.lab.gateway.route;

import reactor.core.publisher.Flux;

public interface RouteDefinitionLocator {

    Flux<RouteDefinition> getRouteDefinitions();
    
}
