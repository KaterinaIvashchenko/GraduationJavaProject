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
public class SimpleExampleWriter {
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

    private static class InfoHandler implements Handler {
        @Override
        public void handle(Request request, Response response) throws Exception {
            response.setContentType("text/html");

            PrintWriter pw = response.getWriter();

            // Set doctype
            pw.println("<!DOCTYPE html>");

            // Write some HTML
            pw.println("<html><body>");

            pw.println("<p>http://localhost:8080/info.html");
            pw.println("<br>");
            pw.println("<p>Requested address: ");
            pw.println(request.getPath());
            pw.println("<br>");
            pw.println("<p>Request method: ");
            pw.println(request.getMethod());
            pw.println("<br>");


            Map<String, String> args = request.getArguments();

            if (!args.isEmpty()) {
                pw.println("<p><strong>Passed arguments:</strong><br>");

                for (Map.Entry<String, String> entry : args.entrySet()) {
                    pw.println("Key: ");
                    pw.print(entry.getKey());
                    pw.println(", Value: ");
                    pw.print(entry.getValue());
                    pw.println("<br>");
                }

                pw.println("</p>");
            }

            Map<String, String> headers = request.getHeaders();

            if (!headers.isEmpty()) {
                pw.println("<p><strong>Passed headers:</strong><br>");

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    pw.println("Key: ");
                    pw.print(entry.getKey());
                    pw.println(", Value: ");
                    pw.print(entry.getValue());
                    pw.println("<br>");
                }

                pw.println("</p>");
            }

            pw.println(" <iframe width=\"420\" height=\"315\"\n" +
                    "src=\"https://www.youtube.com/embed/dQw4w9WgXcQ\">\n" +
                    "</iframe> ");

            pw.println("</body></html>");
        }
    }
}
