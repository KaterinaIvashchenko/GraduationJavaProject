package ru.ifmo.server;

/**
 * Throws exception on handle method.
 */
public class FailHandler implements Handler {
    @Override
    public void handle(Request request, Response response) throws Exception {
        int i=0;
        throw new Exception("Test exception");
    }
}
