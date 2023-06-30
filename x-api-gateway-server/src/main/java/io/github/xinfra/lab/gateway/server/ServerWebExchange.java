package io.github.xinfra.lab.gateway.server;

import io.github.xinfra.lab.gateway.commons.Assert;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.HashMap;
import java.util.Map;

public class ServerWebExchange {
    private HttpServerRequest request;
    private HttpServerResponse response;
    private Map<String, Object> attributes = new HashMap<>();

    public ServerWebExchange(HttpServerRequest request, HttpServerResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServerRequest getRequest() {
        return request;
    }

    public HttpServerResponse getResponse() {
        return response;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }


    public <T> T getRequiredAttribute(String name) {
        T t = getAttribute(name);
        Assert.notNull(t, "Required attribute '" + name + "' is missing");
        return t;
    }

    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        T t = getAttribute(name);
        if (t == null)
            t = defaultValue;
        return t;
    }

    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }
}
