package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Simple example that shows basic usage.
 */
public class SimpleExample {
    public static void main(String[] args) throws URISyntaxException, IOException {

        Handler printHandler = new InfoHandler();

        // Define config with request handlers
        ServerConfig config = new ServerConfig()
                .addHandler("/info.html", printHandler)
                .addHandler("/info", printHandler);

        // Start server
        @SuppressWarnings("unused")
        Server server = Server.start(config);

        // And open it!
        String infoPage = "http://localhost:" + ServerConfig.DFLT_PORT + "/info.html";

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(infoPage));
        }
        else
            System.out.println(">>> Open " + infoPage);

    }

    public static class InfoHandler implements Handler {
        @Override
        public void handle(Request request, Response response) throws Exception {
            // Set correct header
            PrintWriter pw = response.getWriter();

            // Set doctype
            pw.append("<!DOCTYPE html>");

            // Write some HTML
            pw.append("<html><body>");

            pw.append("<p>http://localhost:8080/info.html").append("<br>");
            pw.append("<p>Requested address: ").append(request.getPath()).append("<br>");
            pw.append("<p>Request method: ").append(request.getMethod().toString()).append("<br>");


            Map<String, String> args = request.getArguments();

            if (!args.isEmpty()) {
                pw.append("<p><strong>Passed arguments:</strong><br>");

                for (Map.Entry<String, String> entry : args.entrySet()) {
                    pw.append("Key: ").append(entry.getKey());
                    pw.append(", Value: ").append(entry.getValue());
                    pw.append("<br>");
                }

                pw.append("</p>");
            }

            Map<String, String> headers = request.getHeaders();

            if (!headers.isEmpty()) {
                pw.append("<p><strong>Passed headers:</strong><br>");

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    pw.append("Key: ").append(entry.getKey());
                    pw.append(", Value: ").append(entry.getValue());
                    pw.append("<br>");
                }

                pw.append("</p>");
            }

            pw.append(" <iframe width=\"420\" height=\"315\"\n" +
                    "src=\"https://www.youtube.com/embed/dQw4w9WgXcQ\">\n" +
                    "</iframe> ");

            pw.append("</body></html>");
        }
    }
}
