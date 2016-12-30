package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import sun.nio.cs.ISO_8859_2;
import sun.nio.cs.UTF_32;

import javax.print.attribute.standard.MediaSize;
import java.awt.*;
import java.awt.image.ImagingOpException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static ru.ifmo.server.TestUtils.*;

/**
 * Tests main server functionality.
 */
public class ServerTest {
    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static final String SUCCESS_URL = "/test_success";
    private static final String NOT_FOUND_URL = "/test_not_found";
    private static final String SERVER_ERROR_URL = "/test_fail";

    private static Server server;
    private static CloseableHttpClient client;

    @BeforeClass
    public static void initialize() {
        ServerConfig cfg = new ServerConfig()
                .addHandler(SUCCESS_URL, new SuccessHandler())
                .addHandler(SERVER_ERROR_URL, new FailHandler());

        server = Server.start(cfg);
        client = HttpClients.createDefault();
    }

    @AfterClass
    public static void stop() {
        IOUtils.closeQuietly(server);
        IOUtils.closeQuietly(client);

        server = null;
        client = null;
    }


    @Test
    public void testSuccess() throws Exception {
        // TODO test headers
        URI uri = new URIBuilder(SUCCESS_URL)
                .addParameter("1", "1")
                .addParameter("2", "2")
                .addParameter("testArg1", "testValue1")
                .addParameter("testArg2", "2")
                .addParameter("testArg3", "testVal3")
                .addParameter("testArg4", "")
                .build();

        HttpGet get = new HttpGet(uri);

        CloseableHttpResponse response = client.execute(host, get);

        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>{1=1, 2=2, testArg1=testValue1, testArg2=2, testArg3=testVal3, testArg4=null}" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPostURLEncoded() throws IOException, URISyntaxException {
        URI uri = new URI(SUCCESS_URL);
        HttpPost post = new HttpPost(uri);

        ArrayList parameters = new ArrayList();
        parameters.add(new BasicNameValuePair("name", "java"));
        parameters.add(new BasicNameValuePair("id", "5"));
        post.setEntity(new UrlEncodedFormEntity(parameters));
        CloseableHttpResponse response = client.execute(host, post);
        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>{name=java, id=5}" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPostTextPlain() throws IOException, URISyntaxException {
        URI uri = new URI(SUCCESS_URL);
        HttpPost post = new HttpPost(uri);

        StringEntity stringEntity = new StringEntity("java");
        stringEntity.setContentType("text/plain");
        post.setEntity(stringEntity);

        CloseableHttpResponse response = client.execute(host, post);

        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>{TextPlain=java}" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPutURLEncoded() throws IOException, URISyntaxException {
        URI uri = new URI(SUCCESS_URL);
        HttpPut put = new HttpPut(uri);

        ArrayList parameters = new ArrayList();
        parameters.add(new BasicNameValuePair("name", "java"));
        parameters.add(new BasicNameValuePair("id", "5"));
        put.setEntity(new UrlEncodedFormEntity(parameters));
        CloseableHttpResponse response = client.execute(host, put);
        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>{name=java, id=5}" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPutTextPlain() throws IOException, URISyntaxException {
        URI uri = new URI(SUCCESS_URL);
        HttpPut put = new HttpPut(uri);

        StringEntity stringEntity = new StringEntity("java");
        stringEntity.setContentType("text/plain");
        put.setEntity(stringEntity);

        CloseableHttpResponse response = client.execute(host, put);

        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>{TextPlain=java}" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testNotFound() throws Exception {
        HttpGet get = new HttpGet(NOT_FOUND_URL);

        CloseableHttpResponse response = client.execute(host, get);

        assertStatusCode(HttpStatus.SC_NOT_FOUND, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testServerError() throws Exception {
        HttpGet get = new HttpGet(SERVER_ERROR_URL);

        CloseableHttpResponse response = client.execute(host, get);

        assertStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }



    @Test
    public void testDelete() throws Exception {
        HttpRequest request = new HttpDelete(SUCCESS_URL);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_OK, response);
    }

    @Test
    public void testHead() throws Exception {
        HttpRequest request = new HttpHead(SUCCESS_URL);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_OK, response);
    }

    @Test
    public void testOptions() throws Exception {
        HttpRequest request = new HttpOptions(SUCCESS_URL);

        assertNotImplemented(request);
    }

    @Test
    public void testTrace() throws Exception {
        HttpRequest request = new HttpTrace(SUCCESS_URL);

        assertNotImplemented(request);
    }

    @Test
    public void testPatch() throws Exception {
        HttpRequest request = new HttpPatch(SUCCESS_URL);

        assertNotImplemented(request);
    }

    private void assertNotImplemented(HttpRequest request) throws Exception {
        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_NOT_IMPLEMENTED, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }


    @Test
    public void testPostWithoutContentType() throws Exception {
        HttpRequest request = new HttpPost(SUCCESS_URL);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPutWithoutContentType() throws Exception {
        HttpRequest request = new HttpPut(SUCCESS_URL);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPutWithoutContent() throws Exception {
        HttpRequest request = new HttpPut(SUCCESS_URL);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPostWithoutContent() throws Exception {
        HttpRequest request = new HttpPost(SUCCESS_URL);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }


}
