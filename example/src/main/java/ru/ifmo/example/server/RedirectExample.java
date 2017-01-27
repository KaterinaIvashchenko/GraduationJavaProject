package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Simple hello world example.
 */
public class RedirectExample {
    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/index8", new Handler() {
                    @Override
                    public void handle(Request request, Response response) throws Exception {
                        response.redirect("https://mail.ru/");
                    }
                });

        Server.start(config);
    }
}