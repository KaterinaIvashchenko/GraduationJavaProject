package ru.ifmo.server;

import static ru.ifmo.server.Http.OK_HEADER;


public class TextPlainHandler implements Handler{
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    @Override
    public void handle(Request request, Response response) throws Exception {
        response.setBody((TEST_RESPONSE +
                "<br>" + request.getBody().getBodyTextPlain() + CLOSE_HTML).getBytes());
    }
}
