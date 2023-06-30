package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class DefaultGatewayFilterChain implements GatewayFilterChain {
    private List<GatewayFilter> filters;
    private int index = 0;

    public DefaultGatewayFilterChain(List<GatewayFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        if (index < filters.size()) {
            return filters.get(index++).filter(exchange, this);
        } else {
            return Mono.empty();
        }
    }
}
