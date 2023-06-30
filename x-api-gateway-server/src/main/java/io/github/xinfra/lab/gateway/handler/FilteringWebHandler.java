package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.commons.OrderedAwareComparator;
import io.github.xinfra.lab.gateway.filter.DefaultGatewayFilterChain;
import io.github.xinfra.lab.gateway.filter.GatewayFilter;
import io.github.xinfra.lab.gateway.route.Route;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

public class FilteringWebHandler implements WebHandler {

    private List<GatewayFilter> globalFilters;

    public FilteringWebHandler(List<GatewayFilter> globalFilters) {
        this.globalFilters = globalFilters;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        List<GatewayFilter> gatewayFilters = route.getFilters();

        List<GatewayFilter> combinedFilters = new ArrayList<>(gatewayFilters);
        combinedFilters.addAll(globalFilters);
        OrderedAwareComparator.sort(combinedFilters);

        return new DefaultGatewayFilterChain(combinedFilters).filter(exchange);
    }

}
