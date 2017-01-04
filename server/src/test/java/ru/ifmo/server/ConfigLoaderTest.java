package ru.ifmo.server;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Tests for ConfigLoader
 */

public class ConfigLoaderTest {

    @Test
    public void testProperties() throws IOException {

        // 1. Create test web-server.properties

        File prop = new File(getClass().getClassLoader().getResource("web-server.properties").getFile());


        // 2. Load with config loader

        ServerConfig config = new ConfigLoader().load(prop);

        // 3. Check

        assertEquals(8081, config.getPort());
        assertEquals(5000, config.getSocketTimeout());


    }

}