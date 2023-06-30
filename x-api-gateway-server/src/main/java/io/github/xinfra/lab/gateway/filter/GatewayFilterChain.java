package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayFilterChain {
    Mono<Void> filter(ServerWebExchange exchange);
}
