package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Example for dispatch all reqest to one handler
 */
public class DispatcherExample {
    private static class RequestDispatcher implements Dispatcher {
        @Override
        public String dispatch(Request request, Response response) {
            // Any request will come here
            return "/microservice";
        }
    }

    private static class MicroserviceHandler implements Handler {
        @Override
        public void handle(Request request, Response response) throws Exception {
            Writer writer = new OutputStreamWriter(response.getOutputStream());
            writer.write(Http.OK_HEADER + "Welcome to microservice!");
            writer.flush();
        }
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/microservice", new MicroserviceHandler())
                .setDispatcher(new RequestDispatcher());

        Server.start(config);
    }
}
