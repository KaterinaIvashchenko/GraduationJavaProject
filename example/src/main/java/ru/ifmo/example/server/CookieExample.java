package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.PrintWriter;
import java.io.StreamCorruptedException;

/**
 * Created by Gil on 08-Jan-17.
 */
public class CookieExample {

    public static void main(String[] args) {

        ServerConfig serverConfig = new ServerConfig();

        Handler handler = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                CookieManager cookieManager = new CookieManager();
                cookieManager.getCookies(request);

                Session session = cookieManager.getSession();

                session.setParams("Name", "Evgeny");
                session.setParams("Surname", "Gil");

                response.setStatusCode(Http.SC_OK);
                response.setContentType("text/plain");

                cookieManager.setCookie(new Cookie("name", "Evgeny", "2000"));
                cookieManager.setCookie(new Cookie("surname", "Gil", "2000"));
                cookieManager.setCookie(new Cookie("password", "1413244", "2000"));

                PrintWriter pw = response.getWriter();
                pw.print("Your name is: " + cookieManager.getCookieValue("name") + "\r\n");
                pw.print("Your surname is: " + cookieManager.getCookieValue("surname") + "\r\n");
                pw.print("JSESSIONID from Cookies: " + cookieManager.getCookieValue("JSESSIONID") + "\r\n");

                pw.print("Session name is: " + session.getParams("Name") + "\r\n");
                pw.print("Session surname is: " + session.getParams("Surname") + "\r\n");
                pw.print("Session id: " + session.getId() + "\r\n");

                response.flushBuffer();
            }
        };

        serverConfig.addHandler("/ckexample", handler);

        Server.start(serverConfig);
    }
}
