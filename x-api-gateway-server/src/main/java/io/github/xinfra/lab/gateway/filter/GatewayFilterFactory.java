package io.github.xinfra.lab.gateway.filter;

import io.github.xinfra.lab.gateway.commons.Configurable;

public interface GatewayFilterFactory<C> extends Configurable<C> {

    String getName();

    GatewayFilter apply(C config);
}
