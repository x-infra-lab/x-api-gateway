package io.github.xinfra.lab.gateway.predicate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePredicateDefinition {
    private String name;
    private String config;
}
