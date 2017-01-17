package ru.ifmo.server;

import static ru.ifmo.server.Http.*;

/**
 * Responds with OK status code, test text in body and parsed params with new response API
 */
public class SuccessHandlerWithHeaders implements Handler{
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    @Override
    public void handle(Request request, Response response) throws Exception {
        response.setContentType("text/html");
        response.setBody((TEST_RESPONSE + "<br>" + request.getArguments() + CLOSE_HTML).getBytes());
    }
}