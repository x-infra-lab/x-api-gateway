package io.github.xinfra.lab.gateway.endpoint;


import io.github.xinfra.lab.gateway.bootstrap.AbstractConfigurable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
import java.util.List;

import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.markResponseCommitted;

@Slf4j
public class HttpEndpointFactory extends
        AbstractConfigurable<HttpEndpointFactory.Config>
        implements EndpointFactory<HttpEndpointFactory.Config> {
    public HttpEndpointFactory() {
        super(HttpEndpointFactory.Config.class);
    }

    @Override
    public String getName() {
        return "Http";
    }

    @Override
    public Endpoint apply(Config config) {
        return exchange -> {

            HttpServerRequest request = exchange.getRequest();
            HttpServerResponse response = exchange.getResponse();

            return HttpClient.create()
                    .headers(headers -> {
                        headers.add(request.requestHeaders());
                    })
                    .request(request.method())
                    .uri(URI.create(config.getHosts().get(0))) // TODO Chooser
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
        };
    }


    @Data
    public static class Config {
        List<String> hosts;
        List<Integer> weights;
    }
}
