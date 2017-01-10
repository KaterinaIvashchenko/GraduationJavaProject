package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.PrintWriter;

/**
 * Created by GilEO on 10.01.2017.
 */
public class CookieExample {

    public static void main(String[] args) {


        Handler handler = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                String name = request.getArguments().get("name");
                String surname = request.getArguments().get("surname");
                String password = request.getArguments().get("password");

                PrintWriter writer = response.getWriter();

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<p><b>Login page</b></p>");
                sb.append("<p>Hello " + name + "</p>");
                if (name == null && surname == null && password == null) {

                    sb.append("<form method=\"POST\" action=\"" + request.getPath() + "\">");
                    sb.append("Name: <input type=\"text\" name=\"name\"></input><br><br>");
                    sb.append("Surname: <input type=\"text\" name=\"surname\"></input><br><br>");
                    sb.append("Password: <input type=\"text\" name=\"password\"></input><br><br>");
                    sb.append("<input type=\"submit\" value=\"submit\"></input>");
                    sb.append("</form>");
                } else {

                    Session session = request.getSession();
                    response.setCookie(new Cookie("JSESSIONID", session.getId()));

                    session.setParams("name", name);
                    session.setParams("surname", surname);
                    session.setParams("password", password);

                    sb.append("<p><a href=\"./page1\">to Page 1</a>&nbsp;");
                    sb.append("<a href=\"./page2\">to Page 2</a></p>");

                }

                sb.append("</body></html>");

                writer.print(sb.toString());

                response.flushBuffer();
//                System.out.println(Server.getSessions());
            }
        };

        Handler handler2 = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                Session session = request.getSession();
                response.setCookie(new Cookie("JSESSIONID", session.getId()));

                String name = session.getParams("name");
                String surname = session.getParams("surname");
                String password = session.getParams("password");

                PrintWriter writer = response.getWriter();

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<p><b>Page 1</b></p>");
                sb.append("<p>Your name from session: " + name + "</p>");
                sb.append("<p>Your surname from session: " + surname + "</p>");
                sb.append("<p>Your password from session: " + password + "</p>");
                sb.append("<a href=\"./page2\">to Page 2</a></p>");
                sb.append("</body></html>");

                writer.print(sb.toString());

                response.flushBuffer();
//                System.out.println(Server.getSessions());
            }
        };

        Handler handler3 = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                Session session = request.getSession();
                response.setCookie(new Cookie("JSESSIONID", session.getId()));

                String name = session.getParams("name");
                String surname = session.getParams("surname");
                String password = session.getParams("password");

                PrintWriter writer = response.getWriter();

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<p><b>Page 2</b></p>");
                sb.append("<p>Your name from session: " + name + "</p>");
                sb.append("<p>Your surname from session: " + surname + "</p>");
                sb.append("<p>Your password from session: " + password + "</p>");
                sb.append("<a href=\"./page1\">to Page 1</a></p>");
                sb.append("</body></html>");

                writer.print(sb.toString());

                response.flushBuffer();
//                System.out.println(Server.getSessions());
            }
        };


        ServerConfig config = new ServerConfig();

        config.addHandler("/login", handler);
        config.addHandler("/page1", handler2);
        config.addHandler("/page2", handler3);

        Server.start(config);

    }
}
