package ru.ifmo.server;

import static ru.ifmo.server.ServerConfig.setCompressionType;

/**
 * Responds with OK status code, test text in body and parsed params.
 */
public class CompressHandler implements Handler {
    public static final String OPEN_HTML = "<html><body>";
    public static final String CLOSE_HTML = "</html></body>";

    public static final String TEST_RESPONSE = OPEN_HTML + "<html><body>Test response";

    public static String aviableCompressions;

    @Override
    public void handle(Request request, Response response) throws Exception {
        setCompressionType(CompressionType.GZIP);
        response.setBody((TEST_RESPONSE +
                "<br>" + request.getArguments() + CLOSE_HTML).getBytes());

        aviableCompressions = request.getAviableCompressions().toString();
    }
}