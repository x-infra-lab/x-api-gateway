package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
}
