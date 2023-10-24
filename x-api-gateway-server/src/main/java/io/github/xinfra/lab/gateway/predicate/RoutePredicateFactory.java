package io.github.xinfra.lab.gateway.predicate;


import io.github.xinfra.lab.gateway.commons.Configurable;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;

public interface RoutePredicateFactory<C> extends Configurable<C> {

    String getName();

    RoutePredicate<ServerWebExchange> apply(C config);

}
