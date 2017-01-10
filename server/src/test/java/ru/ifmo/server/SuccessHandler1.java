package ru.ifmo.server;

import static ru.ifmo.server.Http.OK_HEADER;

public class SuccessHandler1 implements Handler {
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response-1";



    @Override
    public void handle(Request request, Response response) throws Exception {
        Thread.currentThread().sleep(3000);
        response.getOutputStream().write((OK_HEADER + TEST_RESPONSE + CLOSE_HTML).getBytes());
        response.getOutputStream().flush();
        multithreadingTest.isFinishedTrue();
    }
}