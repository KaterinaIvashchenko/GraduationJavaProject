package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.PrintWriter;

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
            response.setStatusCode(Http.SC_OK);
            response.setContentType("text/plain");

            PrintWriter pw = response.getWriter();
            pw.print("Welcome to microservice!");
        }
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/microservice", new MicroserviceHandler())
                .setDispatcher(new RequestDispatcher());

        Server.start(config);
    }
}
