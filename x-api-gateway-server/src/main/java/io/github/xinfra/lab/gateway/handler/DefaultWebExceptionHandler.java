package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.commons.GatewayHeaders;
import io.github.xinfra.lab.gateway.exception.ErrorCode;
import io.github.xinfra.lab.gateway.exception.ErrorResponseException;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;


import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.isResponseCommitted;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.markResponseCommitted;


public class DefaultWebExceptionHandler implements WebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable t) {
        if (isResponseCommitted(exchange)) {
            return Mono.empty();
        }
        return Mono.<Void>fromRunnable(() -> {

            ErrorCode errorCode;
            HttpResponseStatus status;
            if (t instanceof ErrorResponseException) {
                ErrorResponseException ex = (ErrorResponseException) t;
                status = ex.getStatus();
                if (ex.getHeaders() != null) {
                    ex.getHeaders().forEach(entry -> exchange.getResponse().addHeader(entry.getKey(), entry.getValue()));
                }
                errorCode = ex.getErrorCode();
            } else {
                // default error info
                errorCode = ErrorCode.SYSTEM_EXCEPTION;
                // default error response status
                status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            }

            exchange.getResponse().status(status);
            exchange.getResponse().addHeader(GatewayHeaders.Names.X_GATEWAY_ERROR_CODE,
                    String.valueOf(errorCode.getCode()));
            exchange.getResponse().addHeader(GatewayHeaders.Names.X_GATEWAY_ERROR_MSG,
                    errorCode.getMsg());
        }).doOnNext(v -> {
            markResponseCommitted(exchange);
        });
    }
}
