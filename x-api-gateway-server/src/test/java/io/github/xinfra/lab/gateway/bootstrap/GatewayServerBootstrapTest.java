package io.github.xinfra.lab.gateway.bootstrap;

import io.github.xinfra.lab.gateway.BaseTest;
import org.junit.Test;
import reactor.netty.DisposableServer;


public class GatewayServerBootstrapTest extends BaseTest {

    @Test
    public void testStartGateWayServer1() {
        DisposableServer server = startGatewayServer(8888);
        server.onDispose().block();
    }
}
