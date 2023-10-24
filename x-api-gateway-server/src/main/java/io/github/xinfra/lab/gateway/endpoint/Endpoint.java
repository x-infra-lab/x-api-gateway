package io.github.xinfra.lab.gateway.endpoint;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface Endpoint<T> {

    T getConfig();
    Mono<Void> invoke(ServerWebExchange exchange);
}
