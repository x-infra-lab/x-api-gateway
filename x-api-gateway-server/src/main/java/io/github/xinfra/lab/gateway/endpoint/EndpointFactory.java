package io.github.xinfra.lab.gateway.endpoint;

import io.github.xinfra.lab.gateway.bootstrap.Configurable;

public interface EndpointFactory<C> extends Configurable<C> {

    String getName();

    Endpoint apply(C config);
}
