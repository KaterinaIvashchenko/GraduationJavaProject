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

                PrintWriter sb = response.getWriter();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<head><title>Java kanban project</title>");
                sb.append("</title>");
                sb.append("<p><b>Login page</b></p><br>");

                if (name == null) {
                    sb.append("<p>Hello new user!</p>");
                } else {
                    sb.append("<p>Hello " + name + "</p>");
                }
                if (name == null && surname == null && password == null) {

                    sb.append("<form method=\"POST\" action=\"" + request.getPath() + "\">");
                    sb.append("Name: <input type=\"text\" name=\"name\"></input><br><br>");
                    sb.append("Surname: <input type=\"text\" name=\"surname\"></input><br><br>");
                    sb.append("Password: <input type=\"password\" name=\"password\"></input><br><br>");
                    sb.append("<input type=\"submit\" value=\"submit\"></input>");
                    sb.append("</form>");
                } else {

                    Session session = request.getSession();

                    session.setParam("name", name);
                    session.setParam("surname", surname);
                    session.setParam("password", password);

                    sb.append("<p><a href=\"./page1\">to Page 1</a>&nbsp;");
                    sb.append("<a href=\"./page2\">to Page 2</a></p>");

                }

                sb.append("</body></html>");

            }
        };
        Handler handler2 = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                Session session = request.getSession();

                String name = session.getParam("name");
                String surname = session.getParam("surname");
                String password = session.getParam("password");

                PrintWriter sb = response.getWriter();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<head><title>Java kanban project</title>");
                sb.append("</title>");
                sb.append("<p><b>Page 1</b></p><br>");
                sb.append("<p>session: " + session.getId() + "</p>");
                sb.append("<p>Your name from session: " + name + "</p>");
                sb.append("<p>Your surname from session: " + surname + "</p>");
                sb.append("<p>Your password from session: " + password + "</p>");
                sb.append("<a href=\"./page2\">to Page 2</a></p>");
                sb.append("<a href=\"./logout\">Exit</a></p>");
                sb.append("</body></html>");

            }
        };
        Handler handler3 = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                Session session = request.getSession();

                String name = session.getParam("name");
                String surname = session.getParam("surname");
                String password = session.getParam("password");

                PrintWriter sb = response.getWriter();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<head><title>Java kanban project</title>");
                sb.append("</title>");
                sb.append("<p><b>Page 2</b></p><br>");
                sb.append("<p>session: " + session.getId() + "</p>");
                sb.append("<p>Your name from session: " + name + "</p>");
                sb.append("<p>Your surname from session: " + surname + "</p>");
                sb.append("<p>Your password from session: " + password + "</p>");
                sb.append("<a href=\"./page1\">to Page 1</a></p>");
                sb.append("<a href=\"./logout\">Exit</a></p>");
                sb.append("</body></html>");

            }
        };
        Handler handler4 = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {

                Session session = request.getSession();

                session.invalidate();

                String name = session.getParam("name");
                String surname = session.getParam("surname");
                String password = session.getParam("password");

                PrintWriter sb = response.getWriter();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<head><title>Java kanban project</title>");
                sb.append("</title>");
                sb.append("<p><b>Logout page</b></p><br>");
                sb.append("<p>session: " + session.getId() + "</p>");
                sb.append("<p>Your name from session: " + name + "</p>");
                sb.append("<p>Your surname from session: " + surname + "</p>");
                sb.append("<p>Your password from session: " + password + "</p>");
                sb.append("<a href=\"./login\">Login</a></p>");
                sb.append("</body></html>");

            }
        };

        ServerConfig config = new ServerConfig();

        config.addHandler("/login", handler);
        config.addHandler("/page1", handler2);
        config.addHandler("/page2", handler3);
        config.addHandler("/logout", handler4);

        Server.start(config);

    }
}
