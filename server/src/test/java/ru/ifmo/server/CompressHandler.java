package ru.ifmo.server;

import static ru.ifmo.server.ServerConfig.setCompressionType;

public class CompressHandler implements Handler {

    public static final String TEST_RESPONSE = "<html><head><title>Java kanban</title></head><body>Test response</body></html>";

    @Override
    public void handle(Request request, Response response) throws Exception {
        setCompressionType(CompressionType.GZIP);
        response.setBody(TEST_RESPONSE.getBytes());
    }
}