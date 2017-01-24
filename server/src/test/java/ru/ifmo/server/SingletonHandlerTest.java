package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class SingletonHandlerTest {

    private static Server server;
    private static ServerConfig cfg;
    private static Handler handler1;
    private static Handler handler2;


    @Before
    public void initialize() {
        cfg = new ServerConfig()
                .addHandler("/succcess", new SuccessHandler())
                .addHandlerClass("/succcess1", SuccessHandler.class)
                .addHandlerClass("/succcess2", SuccessHandler.class);

        server = Server.start(cfg);

        handler1 = this.cfg.handler("/succcess1");
        handler2 = this.cfg.handler("/succcess2");

    }

    @After
    public void close() {
        IOUtils.closeQuietly(server);
        server = null;
        handler1 = null;
        handler2 = null;
    }

    @Test
    public void testCreateSinglentonHandler() {
        assertTrue(handler1 == handler2);
    }

    @Test
    public void testCreateSinglentonHandlerHashCode() {
        assertTrue(handler1.hashCode() == handler2.hashCode());
    }

}
