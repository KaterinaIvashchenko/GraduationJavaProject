package ru.ifmo.server;

import ru.ifmo.server.annotation.URL;

import java.io.IOException;

public class ScanClassHandler {

    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    @URL(method = HttpMethod.GET, value = "/scanGET")
    public void indexScanClassGET(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
    }

    @URL(method = HttpMethod.ANY, value = "/scanANY")
    public void indexScanClassANY(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
    }

    @URL(method = {HttpMethod.HEAD, HttpMethod.GET}, value = "/scan/get")
    public void indexScanClassGETorHEAD1(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
    }

    @URL(method = {HttpMethod.GET, HttpMethod.HEAD}, value = "/scan/head")
    public void indexScanClassGETorHEAD2(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
    }

    @URL(method = {HttpMethod.GET, HttpMethod.DELETE}, value = "/scan/error")
    public void indexScanClassInvalidMethod(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
    }

    @URL(method = HttpMethod.GET, value = "/userException")
    public void throwException(Request request, Response response) throws Exception {
        throw new Exception("oops!");
    }
}
