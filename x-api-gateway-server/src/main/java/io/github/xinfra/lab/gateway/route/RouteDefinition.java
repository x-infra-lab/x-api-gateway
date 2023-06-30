package io.github.xinfra.lab.gateway.route;


import io.github.xinfra.lab.gateway.endpoint.EndpointDefinition;
import io.github.xinfra.lab.gateway.filter.GatewayFilterDefinition;
import io.github.xinfra.lab.gateway.predicate.RoutePredicateDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDefinition {
    private String id;
    private List<RoutePredicateDefinition> predicates;
    private List<GatewayFilterDefinition> filters;
    private EndpointDefinition endpoint;
    private int order;
}
