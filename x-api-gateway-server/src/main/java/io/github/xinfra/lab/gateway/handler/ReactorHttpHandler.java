package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class ReactorHttpHandler {

    private WebHandler webHandler;

    public ReactorHttpHandler(WebHandler webHandler) {
        this.webHandler = webHandler;
    }

    public Publisher<Void> handle(HttpServerRequest request, HttpServerResponse response) {
        ServerWebExchange exchange = new ServerWebExchange(request, response);
        return webHandler.handle(exchange);
    }
}
