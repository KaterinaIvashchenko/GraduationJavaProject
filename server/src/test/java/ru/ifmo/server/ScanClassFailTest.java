package ru.ifmo.server;


import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.*;
import ru.ifmo.server.scanClassFailHandlers.ScanClassInvalidModifier;
import ru.ifmo.server.scanClassFailHandlers.ScanClassInvalidParameters;
import ru.ifmo.server.scanClassFailHandlers.ScanClassInvalidType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class ScanClassFailTest {
    private static Server server;
    private CloseableHttpClient client;
    private static ServerConfig cfg;

    @Before
    public void init() {
        client = HttpClients.createDefault();
        cfg = new ServerConfig();
    }

    @After
    public void close() {
        IOUtils.closeQuietly(client);
        client = null;

        IOUtils.closeQuietly(server);
        server = null;
    }

    @Test(expected = ServerReflectException.class)
    public void testFailParameters() throws ServerReflectException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidParameters.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }

    @Test(expected = ServerReflectException.class)
    public void testFailType() throws ServerReflectException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidType.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }

    @Test(expected = ServerReflectException.class)
    public void testFailModifier() throws ServerReflectException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidModifier.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }
}
