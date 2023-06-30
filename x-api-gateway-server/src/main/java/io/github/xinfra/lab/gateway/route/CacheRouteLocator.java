package io.github.xinfra.lab.gateway.route;

import reactor.core.publisher.Flux;

import java.util.List;


public class CacheRouteLocator implements RouteLocator {

    private RouteLocator delegate;

    private List<Route> cache;

    public CacheRouteLocator(RouteLocator delegate) {
        this.delegate = delegate;
        this.cache = fetch();
    }

    private List<Route> fetch() {
        return delegate.getRoutes().collectList().block();
    }

    @Override
    public Flux<Route> getRoutes() {
        return Flux.fromIterable(cache);
    }


    public void refresh() {
        this.cache = fetch();
    }
}
