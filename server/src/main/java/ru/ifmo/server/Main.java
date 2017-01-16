package ru.ifmo.server;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Ars on 30.12.2016.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {


        ServerConfig config = new ServerConfig()
                      .addHandler("/index", new Handler() {
                    @Override
                    public void handle(Request request, Response response) throws Exception {
                        Writer writer = new OutputStreamWriter(response.getOutputStream());
                        writer.write(Http.OK_HEADER + "Hello World! ");
                        writer.write(Thread.currentThread().getName());
                        writer.flush();
                    }
                });

        Server server = Server.start(config);


    }
}
