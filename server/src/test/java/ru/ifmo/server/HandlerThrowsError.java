package ru.ifmo.server;

import static ru.ifmo.server.Http.OK_HEADER;

public class HandlerThrowsError implements Handler {

    public HandlerThrowsError() throws ServerException {
        throw new ServerException ("Test error HandlerThrowsError");
    }

    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test HandlerThrowsError";

    @Override
    public void handle(Request request, Response response) throws Exception {
        response.setBody((OK_HEADER + TEST_RESPONSE + CLOSE_HTML).getBytes());

    }
}
