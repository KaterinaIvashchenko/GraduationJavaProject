package ru.ifmo.example.server;


import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class HttpMethodsExample {
    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/example", new Handler() {
                    @Override
                    public void handle(Request request, Response response) throws Exception {

                        PrintWriter writer = response.getWriter();
                        if (request.getMethod().equals(HttpMethod.GET) ||
                                request.getMethod().equals(HttpMethod.DELETE) ||
                                request.getMethod().equals(HttpMethod.HEAD)) {
                            writer.write("Request method: " + request.getMethod());
                        }

                        if (request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT)) {
                            if (request.getBody().getContentType().equals("text/plain")) {
                                writer.write("Request method: " + request.getMethod() + '\n' +
                                        "Content-Type: " + request.getBody().getContentType() + '\n' +
                                        "Body text: " + request.getBody().getBodyTextPlain());
                            }
                            if (request.getBody().getContentType().equals("application/x-www-form-urlencoded")) {
                                writer.write("Request method: " + request.getMethod() + '\n' +
                                        "Content-Type: " + request.getBody().getContentType() + '\n' +
                                        "Body arguments: " + request.getArguments());
                            }
                        }

                    }
                });

        Server.start(config);
    }
}
