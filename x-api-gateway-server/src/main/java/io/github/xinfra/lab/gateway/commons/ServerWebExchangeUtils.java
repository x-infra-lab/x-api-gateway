package io.github.xinfra.lab.gateway.commons;

import io.github.xinfra.lab.gateway.server.ServerWebExchange;

public class ServerWebExchangeUtils {
    /**
     * Gateway route attribute name.
     */
    public static final String GATEWAY_ROUTE_ATTR = "gatewayRoute";

    public static final String RESPONSE_COMMITTED_ATTR = "responseCommitted";

    public static final String GATEWAY_ALREADY_ROUTED_ATTR = "gatewayAlreadyRouted";

    public static boolean isResponseCommitted(ServerWebExchange exchange) {
        Boolean committed = exchange.getAttribute(RESPONSE_COMMITTED_ATTR);
        return committed == null ? false : committed;
    }

    public static void markResponseCommitted(ServerWebExchange exchange) {
        if (isResponseCommitted(exchange)) {
            throw new IllegalStateException("response has committed.");
        }
        exchange.getAttributes().put(RESPONSE_COMMITTED_ATTR, Boolean.TRUE);
    }

    public static boolean isAlreadyRouted(ServerWebExchange exchange) {
        return exchange.getAttributeOrDefault(GATEWAY_ALREADY_ROUTED_ATTR, false);
    }

    public static void setAlreadyRouted(ServerWebExchange exchange) {
        exchange.getAttributes().put(GATEWAY_ALREADY_ROUTED_ATTR, true);
    }

    /**
     * simple description for exchange
     *
     * @param exchange
     * @return
     */
    public static String desc(ServerWebExchange exchange) {
        StringBuilder out = new StringBuilder();
        out.append("Exchange: ");
        out.append(exchange.getRequest().method());
        out.append(" ");
        out.append(exchange.getRequest().uri());
        return out.toString();
    }
}
