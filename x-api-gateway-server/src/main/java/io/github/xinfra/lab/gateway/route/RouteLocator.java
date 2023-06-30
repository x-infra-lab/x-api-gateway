package io.github.xinfra.lab.gateway.route;

import reactor.core.publisher.Flux;


public interface RouteLocator {

    Flux<Route> getRoutes();
}
