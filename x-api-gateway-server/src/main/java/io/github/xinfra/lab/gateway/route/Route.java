package io.github.xinfra.lab.gateway.route;

import io.github.xinfra.lab.gateway.endpoint.Endpoint;
import io.github.xinfra.lab.gateway.filter.GatewayFilter;
import io.github.xinfra.lab.gateway.predicate.RoutePredicate;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;

import java.util.List;

public interface Route {

    String getId();

    RoutePredicate<ServerWebExchange> getPredicate();

    List<GatewayFilter> getFilters();

    Endpoint getEndpoint();
}
