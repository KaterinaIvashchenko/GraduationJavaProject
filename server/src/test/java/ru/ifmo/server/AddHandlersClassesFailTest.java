package ru.ifmo.server;


import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddHandlersClassesFailTest {
    private static Server server;
    private static ServerConfig cfg;

    @Before
    public void init() {
        cfg = new ServerConfig()
                .addHandlerClass("/errorHandler", HandlerThrowsError.class);

    }

    @After
    public void close() {
        IOUtils.closeQuietly(server);
        server = null;
    }

    @Test(expected = ServerException.class)
    public void testFailParameters() throws ServerException {

        server = Server.start(cfg);
    }

}
