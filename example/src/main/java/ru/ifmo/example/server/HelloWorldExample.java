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
                .addHandler("/index2", new Handler() {

                    @Override
                    public void handle(Request request, Response response) throws Exception {
                        //response.setStatusCode(301);
                        //response.setHeader("Location", "https://habrahabr.ru");
                        response.redirect("https://habrahabr.ru");
                        //System.out.println(response.getHeaders());
                    }
                });

        Server.start(config);
    }
}
