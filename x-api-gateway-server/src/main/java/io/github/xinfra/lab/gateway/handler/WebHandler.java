package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface WebHandler {
    Mono<Void> handle(ServerWebExchange exchange);
}
