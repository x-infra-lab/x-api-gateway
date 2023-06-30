package io.github.xinfra.lab.gateway.route;


import io.github.xinfra.lab.gateway.commons.binder.Binder;
import io.github.xinfra.lab.gateway.commons.binder.YmlBinder;
import io.github.xinfra.lab.gateway.endpoint.Endpoint;
import io.github.xinfra.lab.gateway.endpoint.EndpointDefinition;
import io.github.xinfra.lab.gateway.endpoint.EndpointFactory;
import io.github.xinfra.lab.gateway.endpoint.EndpointFactoryManager;
import io.github.xinfra.lab.gateway.filter.GatewayFilter;
import io.github.xinfra.lab.gateway.filter.GatewayFilterDefinition;
import io.github.xinfra.lab.gateway.filter.GatewayFilterFactory;
import io.github.xinfra.lab.gateway.filter.GatewayFilterFactoryManager;
import io.github.xinfra.lab.gateway.predicate.RoutePredicate;
import io.github.xinfra.lab.gateway.predicate.RoutePredicateDefinition;
import io.github.xinfra.lab.gateway.predicate.RoutePredicateFactory;
import io.github.xinfra.lab.gateway.predicate.RoutePredicateFactoryManager;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class RouteDefinitionRouteLocator implements RouteLocator {

    private RouteDefinitionLocator routeDefinitionLocator;

    private Binder binder = new YmlBinder();

    private EndpointFactoryManager endpointFactoryManager = EndpointFactoryManager.INSTANCE;

    private RoutePredicateFactoryManager routePredicateFactoryManager = RoutePredicateFactoryManager.INSTANCE;

    private GatewayFilterFactoryManager gatewayFilterFactoryManager = GatewayFilterFactoryManager.INSTANCE;

    public RouteDefinitionRouteLocator(RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Override
    public Flux<Route> getRoutes() {
        return routeDefinitionLocator.getRouteDefinitions().map(this::buildRoute);
    }

    private Route buildRoute(RouteDefinition routeDefinition) {
        DefaultRoute route = new DefaultRoute();
        route.setId(routeDefinition.getId());
        route.setOrder(routeDefinition.getOrder());
        route.setPredicate(combinePredicates(routeDefinition.getPredicates()));
        route.setFilters(buildGatewayFilters(routeDefinition.getFilters()));
        route.setEndpoint(buildEndpoint(routeDefinition.getEndpoint()));
        return route;
    }

    private Endpoint buildEndpoint(EndpointDefinition endpoint) {
        EndpointFactory endpointFactory = endpointFactoryManager.lookup(endpoint.getName());
        Class configClass = endpointFactory.getConfigClass();
        Object config = this.binder.binder(configClass, endpoint.getConfig());
        return endpointFactory.apply(config);
    }

    private List<GatewayFilter> buildGatewayFilters(List<GatewayFilterDefinition> filterDefinitions) {
        if (filterDefinitions == null) {
            return Collections.emptyList();
        }
        List<GatewayFilter> filters = new ArrayList<>();
        filterDefinitions.forEach(definition -> {
            GatewayFilterFactory filterFactory = gatewayFilterFactoryManager.lookup(definition.getName());
            Class configClass = filterFactory.getConfigClass();
            Object config = this.binder.binder(configClass, definition.getConfig());
            filters.add(filterFactory.apply(config));
        });
        return filters;
    }

    private RoutePredicate<ServerWebExchange> combinePredicates(List<RoutePredicateDefinition> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return exchange -> Mono.just(true);
        }

        RoutePredicate<ServerWebExchange> routePredicate = buildRoutePredicate(predicates.get(0));

        for (RoutePredicateDefinition andPredicate : predicates.subList(1, predicates.size())) {
            routePredicate = routePredicate.and(buildRoutePredicate(andPredicate));
        }
        return routePredicate;
    }

    private RoutePredicate<ServerWebExchange> buildRoutePredicate(RoutePredicateDefinition routePredicateDefinition) {
        String name = routePredicateDefinition.getName();
        RoutePredicateFactory routePredicateFactory = routePredicateFactoryManager.lookup(name);
        if (routePredicateFactory == null) {
            throw new IllegalArgumentException("routePredicateFactory named:" + name + " not found");
        }
        Class configClass = routePredicateFactory.getConfigClass();
        Object config = this.binder.binder(configClass, routePredicateDefinition.getConfig());
        return routePredicateFactory.apply(config);
    }
}
