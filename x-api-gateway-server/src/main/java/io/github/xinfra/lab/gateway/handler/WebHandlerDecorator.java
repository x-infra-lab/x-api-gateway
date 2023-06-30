package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.commons.Assert;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WebHandlerDecorator implements WebHandler {

    private final WebHandler delegate;

    public WebHandlerDecorator(WebHandler delegate) {
        Assert.notNull(delegate, "'delegate' must not be null");
        this.delegate = delegate;
    }


    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return this.delegate.handle(exchange);
    }
}