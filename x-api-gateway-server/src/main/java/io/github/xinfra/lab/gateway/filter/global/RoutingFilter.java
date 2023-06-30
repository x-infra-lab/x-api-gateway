package io.github.xinfra.lab.gateway.filter.global;

import io.github.xinfra.lab.gateway.endpoint.Endpoint;
import io.github.xinfra.lab.gateway.filter.GatewayFilterChain;
import io.github.xinfra.lab.gateway.route.Route;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.isAlreadyRouted;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.setAlreadyRouted;

public class RoutingFilter implements GlobalGatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isAlreadyRouted(exchange)) {
            return chain.filter(exchange);
        }
        setAlreadyRouted(exchange);
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        Endpoint endpoint = route.getEndpoint();
        return endpoint.invoke(exchange)
                .then(chain.filter(exchange));
    }
}
