package io.github.xinfra.lab.gateway.route;


import io.github.xinfra.lab.gateway.commons.Ordered;
import io.github.xinfra.lab.gateway.endpoint.Endpoint;
import io.github.xinfra.lab.gateway.filter.GatewayFilter;
import io.github.xinfra.lab.gateway.predicate.RoutePredicate;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import lombok.Data;

import java.util.List;

@Data
public class DefaultRoute implements Route, Ordered {

    private String id;

    private RoutePredicate<ServerWebExchange> predicate;

    private List<GatewayFilter> filters;

    private Endpoint endpoint;

    private int order = Ordered.LOWEST_PRECEDENCE;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public RoutePredicate<ServerWebExchange> getPredicate() {
        return this.predicate;
    }

    @Override
    public List<GatewayFilter> getFilters() {
        return this.filters;
    }

    @Override
    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
