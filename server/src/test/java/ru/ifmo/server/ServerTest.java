package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.ifmo.server.Http.*;
import static ru.ifmo.server.TestUtils.assertStatusCode;

/**
 * Tests main server functionality.
 */
public class ServerTest {
    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static final String SUCCESS_URL = "/test_success";
    private static final String SUCCESS_URL_NEW = "/test_success_new";
    private static final String NOT_FOUND_URL = "/test_not_found";
    private static final String SERVER_ERROR_URL = "/test_fail";
    private static final String TEXT_PLAIN_URL = "/test_text_plain";

    private static Server server;
    private CloseableHttpClient client;

    @BeforeClass
    public static void initialize() {
        ServerConfig cfg = new ServerConfig()
                .addHandler(SUCCESS_URL, new SuccessHandler())
                .addHandler(SUCCESS_URL_NEW, new SuccessHandlerNew())
                .addHandler(SERVER_ERROR_URL, new FailHandler())
                .addHandler(TEXT_PLAIN_URL, new TextPlainHandler())
                .addHandler(SERVER_ERROR_URL, new FailHandler())
                .addHandler(DispatcherTest.DISPATCHED_URL,new DispatchHandler())
                .setDispatcher(new DispatcherTest());

        server = Server.start(cfg);

    }

    @AfterClass
    public static void stop() {
        IOUtils.closeQuietly(server);

        server = null;
    }

    @Before
    public void init() {
        client = HttpClients.createDefault();
    }

    @After
    public void close() {
        IOUtils.closeQuietly(client);
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

        List<BasicNameValuePair> parameters = new ArrayList<>();
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
        URI uri = new URI(TEXT_PLAIN_URL);
        HttpPost post = new HttpPost(uri);

        StringEntity stringEntity = new StringEntity("java");
        stringEntity.setContentType(MIME_TEXT_PLAIN);
        post.setEntity(stringEntity);

        CloseableHttpResponse response = client.execute(host, post);

        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>java" +
                        SuccessHandler.CLOSE_HTML,
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPutURLEncoded() throws IOException, URISyntaxException {
        URI uri = new URI(SUCCESS_URL);
        HttpPut put = new HttpPut(uri);

        List<BasicNameValuePair> parameters = new ArrayList<>();
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
        URI uri = new URI(TEXT_PLAIN_URL);
        HttpPut put = new HttpPut(uri);

        StringEntity stringEntity = new StringEntity("java");
        stringEntity.setContentType(MIME_TEXT_PLAIN);
        put.setEntity(stringEntity);

        CloseableHttpResponse response = client.execute(host, put);

        assertStatusCode(HttpStatus.SC_OK, response);
        assertEquals(SuccessHandler.TEST_RESPONSE +
                        "<br>java" +
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

    @Test
    public void testDispatcher() throws Exception {
        HttpGet get = new HttpGet(DispatcherTest.FOR_DISPATCH_URL);

        CloseableHttpResponse response = client.execute(host, get);
        String responseBody = EntityUtils.toString(response.getEntity());

        assertEquals("Path not dispatched", responseBody.contains("Test dispatch"), true);
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
    }

    @Test
    public void testPutWithoutContentType() throws Exception {
        HttpRequest request = new HttpPut(SUCCESS_URL);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
    }

    @Test
    public void testPutWithoutContent() throws Exception {
        HttpRequest request = new HttpPut(SUCCESS_URL);
        request.setHeader(HEADER_NAME_CONTENT_TYPE, MIME_URL_ENCODED);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
    }

    @Test
    public void testPostWithoutContent() throws Exception {
        HttpRequest request = new HttpPost(SUCCESS_URL);
        request.setHeader(HEADER_NAME_CONTENT_TYPE, MIME_URL_ENCODED);

        CloseableHttpResponse response = client.execute(host, request);

        assertStatusCode(HttpStatus.SC_BAD_REQUEST, response);
    }

    @Test
    public void testStatusCode() throws Exception {
        HttpGet get = new HttpGet(SUCCESS_URL_NEW);

        CloseableHttpResponse response = client.execute(host, get);

        assertStatusCode(HttpStatus.SC_OK, response);
    }

    @Test
    public void testSuccessNew() throws Exception {
        URI uri = new URIBuilder(SUCCESS_URL_NEW)
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

        //test response headers
        assertEquals("Response not contains header Content-Length", response.containsHeader("Content-Length"),true);
        assertEquals("Response not contains header Content-Type", response.containsHeader("Content-Type"),true);

    }


}
