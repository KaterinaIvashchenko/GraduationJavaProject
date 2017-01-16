package ru.ifmo.server;

import java.io.IOException;

import static ru.ifmo.server.Http.OK_HEADER;

public class SuccessHandler1 implements Handler {
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response-1";

    @Override
    public void handle(Request request, Response response) throws IOException {
        MultithreadingTest.isFinishedTrue();
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
            System.out.println("Thread is Interrupted during sleep");
        }
        response.getOutputStream().write((OK_HEADER + TEST_RESPONSE + CLOSE_HTML).getBytes());
        response.getOutputStream().flush();

    }
}