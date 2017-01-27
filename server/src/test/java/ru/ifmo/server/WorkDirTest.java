package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static junit.framework.TestCase.assertEquals;
import static ru.ifmo.server.TestUtils.assertStatusCode;

/**
 * Created by nexxie on 27.01.2017.
 */
public class WorkDirTest {
        private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);
        private static Server server;
        private CloseableHttpClient client;
        private static ServerConfig cfg;

        @Before
        public void init() {
            cfg = new ServerConfig();
            cfg.setWorkDir(new File("D:\\JavaCourse2\\GraduationJavaProject\\example\\src\\main\\resources"));
            server = Server.start(cfg);
            client = HttpClients.createDefault();
        }

        @After
        public void close() {
            IOUtils.closeQuietly(client);
            client = null;

            IOUtils.closeQuietly(server);
            server = null;
        }

        @Test
        public void testFindFile() throws IOException, URISyntaxException {
            URI uri = new URI("/web-server.xml");
            HttpGet get = new HttpGet(uri);
            CloseableHttpResponse response = client.execute(host, get);

            assertStatusCode(HttpStatus.SC_OK, response);
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                            "<config>\n" +
                            " <port>8081</port>\n" +
                            " <socketTimeout>5000</socketTimeout>\n" +
                            " <handlers>\n" +
                            " <handler url=\"/success\">ru.ifmo.server.SuccessHandler</handler>\n" +
                            " </handlers>\n" +
                            "</config>",
                    EntityUtils.toString(response.getEntity()));
        }
    }

