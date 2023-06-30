package io.github.xinfra.lab.gateway.config;

import io.github.xinfra.lab.gateway.filter.GatewayFilterDefinition;
import io.github.xinfra.lab.gateway.route.RouteDefinition;
import lombok.Data;

import java.util.List;

@Data
public class GatewayConfig {

    List<GatewayFilterDefinition> defaultFilters;

    List<RouteDefinition> routes;

}
