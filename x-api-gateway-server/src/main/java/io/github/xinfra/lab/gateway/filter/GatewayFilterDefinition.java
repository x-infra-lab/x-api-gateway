package io.github.xinfra.lab.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayFilterDefinition {

    private String name;

    private String config;
}
