package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NoopGatewayFilter implements GatewayFilter{
    public static final NoopGatewayFilter INSTANCE = new NoopGatewayFilter();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.empty();
    }
}
