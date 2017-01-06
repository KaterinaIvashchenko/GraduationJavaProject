package ru.ifmo.server;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/** Tests for ConfigLoader */

public class ConfigLoaderTest {

    @Test
    public void testProperties() throws IOException {

        // 1.Create test web-server.properties

        File prop = new File(getClass().getClassLoader().getResource("web-server.properties").getFile());

        // 2.Load with config loader

        ServerConfig config = new ConfigLoader().load(prop);

        // 3.Check

        assertEquals(8081, config.getPort());
        assertEquals(5000, config.getSocketTimeout());

        Set<String> paths = new HashSet<>(Arrays.asList("/success"));

        assertEquals(paths, config.getHandlers().keySet());
        assertNotNull(config.getHandlers().get("/success"));
        assertEquals(SuccessHandler.class, config.getHandlers().get("/success").getClass());
    }

}