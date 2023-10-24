package io.github.xinfra.lab.gateway.endpoint;


import io.github.xinfra.lab.gateway.commons.AbstractConfigurable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpEndpointFactory extends
        AbstractConfigurable<HttpEndpoint.Config>
        implements EndpointFactory<HttpEndpoint.Config> {
    public static final String NAME = "Http";

    public HttpEndpointFactory() {
        super(HttpEndpoint.Config.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Endpoint apply(HttpEndpoint.Config config) {
        return new HttpEndpoint(config);
    }

}
