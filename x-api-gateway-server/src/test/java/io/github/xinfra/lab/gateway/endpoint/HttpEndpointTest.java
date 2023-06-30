package io.github.xinfra.lab.gateway.endpoint;

import io.github.xinfra.lab.gateway.BaseTest;
import io.github.xinfra.lab.gateway.common.TestSocketUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;


@Slf4j
public class HttpEndpointTest extends BaseTest {

    @Test
    public void httpEndpointTest1() {

        int gatewayServerPort = TestSocketUtils.findAvailableTcpPort();
        DisposableServer gatewayServer = startGatewayServer(gatewayServerPort);
        log.info("gatewayServer started.");

        // see config.yml
        int httpServerPort = 9999;
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
        gatewayServer.disposeNow();
    }
}
