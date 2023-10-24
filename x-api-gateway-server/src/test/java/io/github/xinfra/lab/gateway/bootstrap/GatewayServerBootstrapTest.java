package io.github.xinfra.lab.gateway.bootstrap;

import com.google.common.collect.Lists;
import io.github.xinfra.lab.gateway.BaseTest;
import io.github.xinfra.lab.gateway.common.TestSocketUtils;
import io.github.xinfra.lab.gateway.route.RouteLocator;
import io.github.xinfra.lab.gateway.route.RouteLocatorBuilder;
import org.junit.Assert;
import org.junit.Test;


public class GatewayServerBootstrapTest extends BaseTest {

    @Test
    public void testStartGateWayServerUseConfigFile1() {
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("httptest.yml").getPath().toString();

        GatewayServerBootstrap bootstrap = startGatewayServer(TestSocketUtils.findAvailableTcpPort(),
                path);
        Assert.assertNotNull(bootstrap);
        bootstrap.stop();
    }

    @Test
    public void testStartGateWayServerUseRouteLocatorBuilder() {

        RouteLocator routeLocator =
                RouteLocatorBuilder.builder()
                        .route("test-http",
                                routePredicateSpec -> routePredicateSpec.path("/test")
                                        .negate()
                                        .filters()
                                        .endpoint()
                                        .http(Lists.newArrayList("http://127.0.0.1:9999/helloworld", "http://localhost:9999/helloworld"),
                                                Lists.newArrayList())
                        )
                        .build();
        GatewayServerBootstrap bootstrap = startGatewayServer(TestSocketUtils.findAvailableTcpPort(),
                routeLocator);
        Assert.assertNotNull(bootstrap);
        bootstrap.stop();
    }

}
