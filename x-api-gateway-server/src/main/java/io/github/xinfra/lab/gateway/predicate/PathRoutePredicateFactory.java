package io.github.xinfra.lab.gateway.predicate;

import io.github.xinfra.lab.gateway.bootstrap.AbstractConfigurable;
import io.github.xinfra.lab.gateway.server.ServerWebExchange;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public class PathRoutePredicateFactory
        extends AbstractConfigurable<PathRoutePredicateFactory.PatternConfig>
        implements RoutePredicateFactory<PathRoutePredicateFactory.PatternConfig> {


    public PathRoutePredicateFactory() {
        super(PatternConfig.class);
    }

    @Override
    public String getName() {
        return "Path";
    }

    @Override
    public RoutePredicate<ServerWebExchange> apply(PatternConfig config) {
        Pattern p = Pattern.compile(config.getPattern());

        return exchange -> {
            String path = exchange.getRequest().path();
            return Mono.just(p.matcher(path).matches());
        };
    }

    @Data
    public static class PatternConfig {
        private String pattern;
    }

}
