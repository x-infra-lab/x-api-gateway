package io.github.xinfra.lab.gateway.bootstrap;

import io.github.xinfra.lab.gateway.BaseTest;
import io.github.xinfra.lab.gateway.common.TestSocketUtils;
import org.junit.Assert;
import org.junit.Test;
import reactor.netty.DisposableServer;


public class GatewayServerBootstrapTest extends BaseTest {

    @Test
    public void testStartGateWayServer1() {
        DisposableServer server = startGatewayServer(TestSocketUtils.findAvailableTcpPort());
        Assert.assertNotNull(server);
        server.disposeNow();
    }
}
