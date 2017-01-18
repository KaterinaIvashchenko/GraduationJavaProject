package ru.ifmo.server;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/** Tests for ConfigLoader */

public class ConfigLoaderTest {

    private static String PATH = "/success";

    @Test
    public void testProperties() throws IOException {

        URL resourse = getClass().getClassLoader().getResource("web-server.properties");

        assertNotNull(resourse);

        File prop = new File(resourse.getFile());

        ServerConfig config = new ConfigLoader().load(prop);

        checkConfig(config);
    }

    @Test
    public void testXml() throws Exception {

        URL resourse = getClass().getClassLoader().getResource("web-server.xml");

        assertNotNull(resourse);

        File prop = new File(resourse.getFile());

        ServerConfig config = new ConfigLoader().load(prop);

        checkConfig(config);
    }

    @Test
    public void testClasspath() throws Exception {
        ServerConfig config = new ConfigLoader().load();

        checkConfig(config);
    }

    private void checkConfig(ServerConfig config) {

        assertEquals(8081, config.getPort());
        assertEquals(5000, config.getSocketTimeout());

        Set<String> paths = new HashSet<>(Collections.singleton(PATH));

        assertEquals(paths, config.getHandlers().keySet());
        assertNotNull(config.getHandlers().get(PATH));
        assertEquals(SuccessHandler.class, config.getHandlers().get(PATH).getClass());
    }
}