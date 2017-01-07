package ru.ifmo.server;

import ru.ifmo.server.annotation.URL;

import java.io.IOException;

import static ru.ifmo.server.Http.OK_HEADER;

public class ScanClassHandler {

    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    @URL(methods = {HttpMethod.GET}, value = "/scanGET")
    public void indexScanClassGET(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.flushBuffer();
    }

    @URL(methods = {HttpMethod.ANY}, value = "/scanANY")
    public void indexScanClassANY(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.flushBuffer();
    }

    @URL(methods = {HttpMethod.HEAD, HttpMethod.GET}, value = "/scan/get")
    public void indexScanClassGETorHEAD1(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.flushBuffer();
    }

    @URL(methods = {HttpMethod.HEAD, HttpMethod.GET}, value = "/scan/head")
    public void indexScanClassGETorHEAD2(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.flushBuffer();
    }

    @URL(methods = {HttpMethod.GET}, value = "/scan/error")
    public void indexScanClassInvalidMethod(Request request, Response response) throws IOException {
        response.setBody((TEST_RESPONSE + "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.flushBuffer();
    }
}
