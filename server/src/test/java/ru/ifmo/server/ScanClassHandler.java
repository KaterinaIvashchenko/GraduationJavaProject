package ru.ifmo.server;

import ru.ifmo.server.annotation.URL;

import java.io.IOException;

import static ru.ifmo.server.Http.OK_HEADER;

public class ScanClassHandler {

    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    @URL(method = HttpMethod.GET, value = "/scan")
    public void indexScanClass(Request request, Response response) throws IOException {
        response.getOutputStream().write((OK_HEADER + TEST_RESPONSE +
                "<br>" + request.getPath() + CLOSE_HTML).getBytes());
        response.getOutputStream().flush();
    }
}
