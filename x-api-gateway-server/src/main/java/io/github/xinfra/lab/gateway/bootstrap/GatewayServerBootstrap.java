package io.github.xinfra.lab.gateway.bootstrap;

import com.google.common.collect.Lists;
import io.github.xinfra.lab.gateway.commons.Assert;
import io.github.xinfra.lab.gateway.config.GatewayConfigManager;
import io.github.xinfra.lab.gateway.filter.global.GlobalGatewayFilter;
import io.github.xinfra.lab.gateway.filter.global.RoutingFilter;
import io.github.xinfra.lab.gateway.handler.DefaultWebExceptionHandler;
import io.github.xinfra.lab.gateway.handler.ExceptionHandlingWebHandler;
import io.github.xinfra.lab.gateway.handler.FilteringWebHandler;
import io.github.xinfra.lab.gateway.handler.ReactorHttpHandler;
import io.github.xinfra.lab.gateway.handler.RoutePredicateWebHandler;
import io.github.xinfra.lab.gateway.handler.WebExceptionHandler;
import io.github.xinfra.lab.gateway.route.CacheRouteLocator;
import io.github.xinfra.lab.gateway.route.GatewayConfigRouteDefinitionLocator;
import io.github.xinfra.lab.gateway.route.RouteDefinitionRouteLocator;
import io.github.xinfra.lab.gateway.route.RouteLocator;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Gateway Server startup
 */
public class GatewayServerBootstrap {

    private int port;
    private List<GlobalGatewayFilter> globalFilters;
    private List<WebExceptionHandler> webExceptionHandlers;
    private RouteLocator routeLocator;
    private String configPath;
    private DisposableServer server;

    public RouteLocator getRouteLocator() {
        return routeLocator;
    }

    public GatewayServerBootstrap port(int port) {
        this.port = port;
        return this;
    }

    public GatewayServerBootstrap globalFilters(List<GlobalGatewayFilter> globalFilters) {
        Assert.notNull(globalFilters, "globalFilters must not be null.");
        this.globalFilters = globalFilters;
        return this;
    }

    public GatewayServerBootstrap webExceptionHandlers(List<WebExceptionHandler> webExceptionHandlers) {
        Assert.notNull(webExceptionHandlers, "webExceptionHandlers must not be null.");
        this.webExceptionHandlers = webExceptionHandlers;
        return this;
    }

    public GatewayServerBootstrap routeLocator(RouteLocator routeLocator) {
        Assert.notNull(routeLocator, "routeLocator must not be null");
        this.routeLocator = routeLocator;
        return this;
    }

    public GatewayServerBootstrap configPath(String configPath) {
        Assert.notNull(configPath, "routeLocator must not be null");
        this.configPath = configPath;
        return this;
    }


    public GatewayServerBootstrap start() {
        if (routeLocator == null) {
            routeLocator = getDefaultRouteLocator();
        }
        if (globalFilters == null) {
            globalFilters = getDefaultGlobalFilters();
        }
        if (webExceptionHandlers == null) {
            webExceptionHandlers = getDefaultWebExceptionHandlers();
        }

        // trigger GatewayFilterChain
        FilteringWebHandler filteringWebHandler = new FilteringWebHandler(globalFilters);
        // route predicate
        RoutePredicateWebHandler routePredicateWebHandler = new RoutePredicateWebHandler(filteringWebHandler, routeLocator);
        // handle exception
        ExceptionHandlingWebHandler exceptionHandlingWebHandler = new ExceptionHandlingWebHandler(routePredicateWebHandler, webExceptionHandlers);
        // reactor-netty-http http handler
        ReactorHttpHandler httpHandler = new ReactorHttpHandler(exceptionHandlingWebHandler);

        // start server
        this.server = HttpServer.create()
                .handle(httpHandler::handle)
                .bindAddress(() -> new InetSocketAddress(port))
                .bind().block();

        return this;
    }

    public GatewayServerBootstrap stop() {
        this.server.disposeNow();
        return this;
    }

    protected List<WebExceptionHandler> getDefaultWebExceptionHandlers() {
        return Lists.newArrayList(new DefaultWebExceptionHandler());
    }

    protected List<GlobalGatewayFilter> getDefaultGlobalFilters() {
        return Lists.newArrayList(new RoutingFilter());
    }

    protected RouteLocator getDefaultRouteLocator() {

        GatewayConfigManager gatewayConfigManager = new GatewayConfigManager();
        if (configPath == null) {
            gatewayConfigManager.loadDefault();
        } else {
            gatewayConfigManager.load(configPath);
        }

        GatewayConfigRouteDefinitionLocator gatewayConfigRouteDefinitionLocator =
                new GatewayConfigRouteDefinitionLocator(gatewayConfigManager.getGatewayConfig());

        RouteLocator routeDefinitionRouteLocator =
                new RouteDefinitionRouteLocator(gatewayConfigRouteDefinitionLocator);
        RouteLocator routeLocator = new CacheRouteLocator(routeDefinitionRouteLocator);
        return routeLocator;
    }
}
