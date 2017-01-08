package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
            response.setStatusCode(Http.SC_OK);
            response.setContentType("text/plain");

//            response.setCookie(new Cookie("name", "name", "2000"));
//            response.setCookie(new Cookie("surname", "surname"));
//            response.setCookie(new Cookie("password", "12341", "2000"));

            PrintWriter pw = response.getWriter();
            pw.print("Welcome to microservice!");
            response.flushBuffer();

//            System.out.println(request.getCookies());
        }
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .addHandler("/microservice", new MicroserviceHandler())
                .setDispatcher(new RequestDispatcher());

        Server.start(config);
    }
}
