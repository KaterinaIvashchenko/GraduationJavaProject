package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class SingletonHandlerTest {

    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static Server server;
    private static ServerConfig cfg;

    private CloseableHttpClient client1;
    private CloseableHttpClient client2;

    @Before
    public void initialize() {
        cfg = new ServerConfig()
                .addHandlerClass("/succcess1", SingletonSuccessHandler.class)
                .addHandlerClass("/succcess2", SingletonSuccessHandler.class);

        server = Server.start(cfg);
    }

    @After
    public void close() {
        IOUtils.closeQuietly(server);
        server = null;
        client1 = null;
        client2 = null;
    }

    @Test
    public void testCreateSinglentonHandler() throws IOException {

        client1 = HttpClients.createDefault();
        client2 = HttpClients.createDefault();

        HttpGet get1 = new HttpGet("/succcess1");
        HttpGet get2 = new HttpGet("/succcess2");

        assertTrue(client1.execute(host, get1).toString().equals(client2.execute(host, get2).toString()));
    }

}
