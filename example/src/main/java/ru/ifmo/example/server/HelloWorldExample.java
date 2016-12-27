package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Simple hello world example.
 */
public class HelloWorldExample {
    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/index", new Handler() {
                    @Override
                    public void handle(Request request, Response response) throws Exception {

                        Writer writer = new OutputStreamWriter(response.getOutputStream());
                        if (request.getMethod().equals(HttpMethod.GET) ||
                                request.getMethod().equals(HttpMethod.DELETE) ||
                                request.getMethod().equals(HttpMethod.HEAD)) {
                            writer.write(Http.OK_HEADER + "Request method: " + request.getMethod());
                        }

                        if (request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT)) {
                            if (request.getContentType().equals("text/plain")) {
                                writer.write(Http.OK_HEADER + "Request method: " + request.getMethod() + '\n' +
                                                "Content-Type: " + request.getContentType() + '\n' +
                                                "Body text: " + request.getBodyTextPlain());
                            }
                            if (request.getContentType().equals("application/x-www-form-urlencoded")) {
                                writer.write(Http.OK_HEADER + "Request method: " + request.getMethod() + '\n' +
                                                "Content-Type: " + request.getContentType() + '\n' +
                                                "Body arguments: " + request.getArguments());
                            }
                        }

                        writer.flush();
                    }
                });

        Server.start(config);
    }
}
