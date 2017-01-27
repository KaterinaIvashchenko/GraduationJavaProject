package ru.ifmo.server;

/**
 * Created by GilEO on 24.01.2017.
 */
public class CookieHandler implements Handler {
    @Override
    public void handle(Request request, Response response) throws Exception {
        response.setBody(request.getHeaders().get("Cookie").getBytes());
    }
}
