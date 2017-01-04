package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Example for dispatch all reqest to one handler
 */
public class DispatcherExample {
    static class RequestDispatcher implements Dispatcher {
        @Override
        public String dispatch(Request request, Response response) {
            return "/microservice";
        }
    }

    static class microserviceHandler implements Handler {
        @Override
        public void handle(Request request, Response response) throws Exception {
            Writer writer = new OutputStreamWriter(response.getOutputStream());
            writer.write(Http.OK_HEADER + "Welcome to microservice!");
            writer.flush();
        }
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/microservice", new microserviceHandler())
                .setDispatcher(new RequestDispatcher());

        Server.start(config);
    }
}
