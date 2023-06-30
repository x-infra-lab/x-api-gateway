package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.commons.Assert;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class ExceptionHandlingWebHandler extends WebHandlerDecorator {

    public static final String HANDLED_WEB_EXCEPTION = ExceptionHandlingWebHandler.class.getSimpleName() + ".handledException";

    private List<WebExceptionHandler> exceptionHandlers;

    public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> exceptionHandlers) {
        super(delegate);
        Assert.notNull(exceptionHandlers, "'exceptionHandlers' must not be null");
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        Mono<Void> completion;
        try {
            completion = super.handle(exchange);
        } catch (Throwable t) {
            completion = Mono.error(t);
        }

        for (WebExceptionHandler handler : exceptionHandlers) {
            completion = completion.doOnError(error -> exchange.getAttributes().put(HANDLED_WEB_EXCEPTION, error))
                    .onErrorResume(error -> handler.handle(exchange, error));
        }
        return completion;
    }
}