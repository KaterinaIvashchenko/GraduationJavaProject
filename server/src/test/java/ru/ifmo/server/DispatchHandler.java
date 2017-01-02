package ru.ifmo.server;

import static ru.ifmo.server.Http.OK_HEADER;

/**
 * For dispatch from ./index
 */
public class DispatchHandler implements Handler {
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test dispatch";

    @Override
    public void handle(Request request, Response response) throws Exception {
        response.getOutputStream().write((OK_HEADER + TEST_RESPONSE +
                "<br>" + request.getArguments() + CLOSE_HTML).getBytes());
        response.getOutputStream().flush();
    }
}
