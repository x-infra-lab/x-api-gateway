package io.github.xinfra.lab.gateway.route;


import io.github.xinfra.lab.gateway.endpoint.Endpoint;
import io.github.xinfra.lab.gateway.endpoint.EndpointFactoryManager;
import io.github.xinfra.lab.gateway.endpoint.HttpEndpoint;
import io.github.xinfra.lab.gateway.endpoint.HttpEndpointFactory;
import io.github.xinfra.lab.gateway.filter.AddHeaderGatewayFilterFactory;
import io.github.xinfra.lab.gateway.filter.GatewayFilter;
import io.github.xinfra.lab.gateway.filter.GatewayFilterFactory;
import io.github.xinfra.lab.gateway.filter.GatewayFilterFactoryManager;
import io.github.xinfra.lab.gateway.predicate.PathRoutePredicateFactory;
import io.github.xinfra.lab.gateway.predicate.RoutePredicate;
import io.github.xinfra.lab.gateway.predicate.RoutePredicateFactoryManager;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.github.xinfra.lab.gateway.route.RouteLocatorBuilder.Operator.AND;
import static io.github.xinfra.lab.gateway.route.RouteLocatorBuilder.Operator.OR;

public class RouteLocatorBuilder {

    List<Route> routeList = new ArrayList<>();

    public static RouteLocatorBuilder builder() {
        return new RouteLocatorBuilder();
    }

    public RouteLocatorBuilder route(String id, Function<RoutePredicateSpec, Route> func) {
        routeList.add(func.apply(new RouteSpec().id(id)));
        return this;
    }

    public RouteLocator build() {
        return () -> Flux.fromIterable(routeList);
    }


    public static class RouteSpec {
        private String id;

        private RoutePredicate<ServerWebExchange> predicate;
        private List<GatewayFilter> gatewayFilterList = new ArrayList<>();

        private Endpoint endpoint;

        RoutePredicateSpec id(String id) {
            this.id = id;
            return new RoutePredicateSpec(this);
        }
    }

    public static class RoutePredicateSpec {
        private RouteSpec routeSpec;
        private Operator operator;

        public RoutePredicateSpec(RouteSpec routeSpec) {
            this.routeSpec = routeSpec;
        }

        public RoutePredicateSpec(RouteSpec routeSpec, Operator operator) {
            this.routeSpec = routeSpec;
            this.operator = operator;
        }

        public BooleanSpec path(String pattern) {
            PathRoutePredicateFactory.PatternConfig config = new PathRoutePredicateFactory.PatternConfig();
            config.setPattern(pattern);
            RoutePredicate predicate = RoutePredicateFactoryManager.INSTANCE.lookup(PathRoutePredicateFactory.NAME)
                    .apply(config);
            if (operator != null) {
                switch (operator) {
                    case AND:
                        routeSpec.predicate = routeSpec.predicate.and(predicate);
                    case OR:
                        routeSpec.predicate = routeSpec.predicate.or(predicate);
                }
            } else {
                routeSpec.predicate = predicate;
            }
            return new BooleanSpec(routeSpec);
        }

    }

    public static class BooleanSpec {
        private RouteSpec routeSpec;

        public BooleanSpec(RouteSpec routeSpec) {
            this.routeSpec = routeSpec;
        }

        public RoutePredicateSpec and() {
            return new RoutePredicateSpec(routeSpec, AND);
        }

        public RoutePredicateSpec or() {
            return new RoutePredicateSpec(routeSpec, OR);
        }

        public BooleanSpec negate() {
            routeSpec.predicate = routeSpec.predicate.negate();
            return this;
        }

        public GatewayFilterSpec filters() {
            return new GatewayFilterSpec(routeSpec);
        }
    }

    public static class GatewayFilterSpec {
        private RouteSpec routeSpec;


        public GatewayFilterSpec(RouteSpec routeSpec) {
            this.routeSpec = routeSpec;
        }

        public GatewayFilterSpec addHeader(Map<String, String> headers) {
            GatewayFilterFactory gatewayFilterFactory = GatewayFilterFactoryManager.INSTANCE
                    .lookup(AddHeaderGatewayFilterFactory.NAME);
            GatewayFilter filter = gatewayFilterFactory.apply(headers);
            routeSpec.gatewayFilterList.add(filter);
            return this;
        }

        public EndpointSpec endpoint() {
            return new EndpointSpec(routeSpec);
        }
    }


    public static class EndpointSpec {
        private RouteSpec routeSpec;

        public EndpointSpec(RouteSpec routeSpec) {
            this.routeSpec = routeSpec;
        }

        public Route http(List<String> urls, List<Integer> weights) {
            HttpEndpoint.Config config = new HttpEndpoint.Config();
            config.setUrls(urls);
            config.setWeights(weights);
            routeSpec.endpoint = EndpointFactoryManager.INSTANCE
                    .lookup(HttpEndpointFactory.NAME)
                    .apply(config);

            // TODO add validate
            DefaultRoute route = new DefaultRoute();
            route.setEndpoint(routeSpec.endpoint);
            route.setFilters(routeSpec.gatewayFilterList);
            route.setPredicate(routeSpec.predicate);
            route.setId(routeSpec.id);
            return route;
        }
    }

    enum Operator {
        AND, OR
    }
}
