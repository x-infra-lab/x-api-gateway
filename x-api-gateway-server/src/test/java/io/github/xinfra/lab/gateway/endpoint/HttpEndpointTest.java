package io.github.xinfra.lab.gateway.endpoint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.xinfra.lab.gateway.BaseTest;
import io.github.xinfra.lab.gateway.bootstrap.GatewayServerBootstrap;
import io.github.xinfra.lab.gateway.common.TestSocketUtils;
import io.github.xinfra.lab.gateway.route.Route;
import io.github.xinfra.lab.gateway.route.RouteLocator;
import io.github.xinfra.lab.gateway.route.RouteLocatorBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;


@Slf4j
public class HttpEndpointTest extends BaseTest {

    @Test
    public void httpEndpointFileConfigTest() throws MalformedURLException {

        int gatewayServerPort = TestSocketUtils.findAvailableTcpPort();

        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("httptest.yml").getPath().toString();

        GatewayServerBootstrap bootstrap = startGatewayServer(gatewayServerPort, path);
        log.info("gatewayServer started.");

        List<Route> routeList = bootstrap.getRouteLocator().getRoutes().collectList().block();
        Route route = routeList.stream()
                .filter(r -> r.getId().equals("http-test"))
                .findFirst()
                .get();

        HttpEndpoint endpoint = (HttpEndpoint) route.getEndpoint();
        String urlStr = endpoint.getConfig().getUrls().get(0);
        URL url = new URL(urlStr);

        // start httpEndpoint
        int httpServerPort = url.getPort();
        DisposableServer httpServer = HttpServer.create()
                .port(httpServerPort)
                .route(routes ->
                        routes.get("/helloworld",
                                (httpServerRequest, httpServerResponse) -> {
                                    System.out.println(httpServerRequest);
                                    return httpServerResponse.sendString(Mono.just("hello world!"));
                                })
                ).bind().block();
        log.info("httpServer started.");


        HttpClientResponse response = HttpClient.create()
                .get()
                .uri(String.format("http://localhost:%s/helloworld", httpServerPort))
                .response()
                .block();
        Assert.assertEquals(response.status(), HttpResponseStatus.OK);
        log.info("httpclient request httpServer success.");


        response = HttpClient.create()
                .get()
                .uri(String.format("http://localhost:%s/hello", gatewayServerPort))
                .response()
                .block();
        Assert.assertEquals(response.status(), HttpResponseStatus.OK);
        log.info("httpclient request gatewayServer success.");

        httpServer.disposeNow();
        bootstrap.stop();
    }

    @Test
    public void httpEndpointApiConfigTest() throws MalformedURLException {
        Map<String, String> addHeadersMap = Maps.newHashMap();
        addHeadersMap.put("proxy", "x-api-gateway");

        RouteLocator routeLocator = RouteLocatorBuilder.builder()
                .route("http-test",
                        routePredicateSpec ->
                                routePredicateSpec.path("hello")
                                        .filters()
                                        .addHeader(addHeadersMap)
                                        .endpoint()
                                        .http(Lists.newArrayList("http://127.0.0.1:9999/helloworld", "http://localhost:9999/helloworld"),
                                                Lists.newArrayList())
                ).build();


        int gatewayServerPort = TestSocketUtils.findAvailableTcpPort();
        GatewayServerBootstrap bootstrap = startGatewayServer(gatewayServerPort, routeLocator);
        log.info("gatewayServer started.");

        List<Route> routeList = bootstrap.getRouteLocator().getRoutes().collectList().block();
        Route route = routeList.stream()
                .filter(r -> r.getId().equals("http-test"))
                .findFirst()
                .get();

        HttpEndpoint endpoint = (HttpEndpoint) route.getEndpoint();
        String urlStr = endpoint.getConfig().getUrls().get(0);
        URL url = new URL(urlStr);

        // start httpEndpoint
        int httpServerPort = url.getPort();
        DisposableServer httpServer = HttpServer.create()
                .port(httpServerPort)
                .route(routes ->
                        routes.get("/helloworld",
                                (httpServerRequest, httpServerResponse) -> {
                                    System.out.println(httpServerRequest);
                                    return httpServerResponse.sendString(Mono.just("hello world!"));
                                })
                ).bind().block();
        log.info("httpServer started.");


        HttpClientResponse response = HttpClient.create()
                .get()
                .uri(String.format("http://localhost:%s/helloworld", httpServerPort))
                .response()
                .block();
        Assert.assertEquals(response.status(), HttpResponseStatus.OK);
        log.info("httpclient request httpServer success.");


        response = HttpClient.create()
                .get()
                .uri(String.format("http://localhost:%s/hello", gatewayServerPort))
                .response()
                .block();
        Assert.assertEquals(response.status(), HttpResponseStatus.OK);
        log.info("httpclient request gatewayServer success.");

        httpServer.disposeNow();
        bootstrap.stop();
    }
}
