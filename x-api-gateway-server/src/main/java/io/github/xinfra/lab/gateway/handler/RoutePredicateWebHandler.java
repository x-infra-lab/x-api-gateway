package io.github.xinfra.lab.gateway.handler;

import io.github.xinfra.lab.gateway.exception.ErrorCode;
import io.github.xinfra.lab.gateway.exception.ErrorResponseException;
import io.github.xinfra.lab.gateway.route.RouteLocator;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static io.github.xinfra.lab.gateway.commons.ServerWebExchangeUtils.desc;

@Slf4j
public class RoutePredicateWebHandler extends WebHandlerDecorator {

    private RouteLocator routeLocator;

    public RoutePredicateWebHandler(WebHandler delegate, RouteLocator routeLocator) {
        super(delegate);
        this.routeLocator = routeLocator;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return routeLocator.getRoutes().concatMap(route ->
                        Mono.just(route)
                                .filterWhen(r -> r.getPredicate().test(exchange))
                                .doOnError(e -> log.error("ErrorCode applying predicate for route: {}", route.getId(), e))
                                .onErrorResume(e -> Mono.empty())
                ).next()
                .map(route -> {
                    log.info("Route matched: {}", route.getId());
                    exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, route);
                    return route;
                }).switchIfEmpty(
                        Mono.defer(() -> {
                                    log.info("No RouteDefinition found for [{}]", desc(exchange));
                                    return Mono.error(new ErrorResponseException(HttpResponseStatus.NOT_FOUND, ErrorCode.ROUTE_NO_MATCH));
                                }
                        )
                ).flatMap(r -> super.handle(exchange));
    }
}