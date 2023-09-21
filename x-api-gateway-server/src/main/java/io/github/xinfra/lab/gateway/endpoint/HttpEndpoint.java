package io.github.xinfra.lab.gateway.endpoint;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
import java.util.List;

import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.markResponseCommitted;

@Slf4j
public class HttpEndpoint implements Endpoint<HttpEndpoint.Config> {

    @Data
    public static class Config {
        List<String> urls;
        List<Integer> weights;
    }

    private Config config;

    public HttpEndpoint(Config config) {
        this.config = config;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public Mono<Void> invoke(ServerWebExchange exchange) {


        HttpServerRequest request = exchange.getRequest();
        HttpServerResponse response = exchange.getResponse();

        return HttpClient.create()
                .headers(headers -> {
                    headers.add(request.requestHeaders());
                })
                .request(request.method())
                .uri(URI.create(config.getUrls().get(0))) // TODO Chooser
                .send(request.receive())
                .response((httpClientResponse, byteBufFlux) ->
                        response.status(httpClientResponse.status())
                                .headers(httpClientResponse.responseHeaders())
                                .sendByteArray(byteBufFlux.asByteArray())
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(t -> {
                    log.error("fail invoke http endpoint:{}",
                            exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR), t);
                }).doOnNext(v -> {
                    markResponseCommitted(exchange);
                }).then();
    }
}
