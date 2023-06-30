package io.github.xinfra.lab.gateway.endpoint;

import io.github.xinfra.lab.gateway.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;


@Slf4j
public class HttpEndpointTest extends BaseTest {

    @Test
    public void httpEndpointTest1() {

        DisposableServer gatewayServer = startGatewayServer(8888);
        log.info("gatewayServer started.");

        DisposableServer httpServer = HttpServer.create()
                .port(9999)
                .route(routes ->
                        routes.get("/helloworld",
                                (httpServerRequest, httpServerResponse) -> {
                                    System.out.println(httpServerRequest);
                                    return httpServerResponse.sendString(Mono.just("hello world!"));
                                })
                ).bind().block();
        log.info("httpServer started.");

        httpServer.onDispose().block();
    }
}
