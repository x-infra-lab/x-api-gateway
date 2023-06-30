package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.bootstrap.AbstractConfigurable;
import reactor.netty.http.server.HttpServerRequest;

import java.util.Map;

public class AddHeaderGatewayFilterFactory extends
        AbstractConfigurable<Map<String, String>>
        implements GatewayFilterFactory<Map<String, String>> {
    public AddHeaderGatewayFilterFactory() {
        super((Class) Map.class);
    }

    @Override
    public String getName() {
        return "AddHeader";
    }

    @Override
    public GatewayFilter apply(Map<String, String> config) {
        return (exchange, chain) -> {
            HttpServerRequest request = exchange.getRequest();
            if (config != null) {
                config.forEach((k, v) -> {
                    request.requestHeaders().add(k, v);
                });
            }
            return chain.filter(exchange);
        };
    }
}
