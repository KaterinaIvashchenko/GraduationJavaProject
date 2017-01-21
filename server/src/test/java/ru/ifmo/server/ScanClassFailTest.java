package ru.ifmo.server;


import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.*;
import ru.ifmo.server.scan.fail.ScanClassInvalidModifier;
import ru.ifmo.server.scan.fail.ScanClassInvalidParameters;
import ru.ifmo.server.scan.fail.ScanClassInvalidType;

import java.util.ArrayList;
import java.util.Collection;

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

    @Test(expected = ServerException.class)
    public void testFailParameters() throws ServerException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidParameters.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }

    @Test(expected = ServerException.class)
    public void testFailType() throws ServerException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidType.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }

    @Test(expected = ServerException.class)
    public void testFailModifier() throws ServerException {
        Collection<Class<?>> classes = new ArrayList<>();
        classes.add(ScanClassInvalidModifier.class);
        ScanClassFailTest.cfg.addClasses(classes);

        ScanClassFailTest.server = Server.start(cfg);
    }
}
