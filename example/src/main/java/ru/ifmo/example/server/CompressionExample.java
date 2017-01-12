package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.PrintWriter;

/**
 * Created by Gil on 12-Jan-17.
 */
public class CompressionExample {

    public static void main(String[] args) {

        ServerConfig config = new ServerConfig();
//        config.setCompressionType(CompressionType.DEFLATE);

        Handler handler = new Handler() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                response.setStatusCode(Http.SC_OK);
                response.setContentType("text/html; charset=utf-8");

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html><body>");
                sb.append("<head><title>Java kanban project</title>");
                sb.append("</head>");
                sb.append("<p>");
                for (int k = 0; k < 3 ; k++) {
                    for (int i = 1; i < 6 ; i++) {
                        sb.append("<font size=\"" + i + "\" >Съешь еще этих мягких французских булок, да выпей же чаю.</font><br>");
                    }
                    for (int j = 6; j > 1 ; j--) {
                        sb.append("<font size=\"" + j + "\" >Съешь ещё этих мягких французских булок, да выпей же чаю.</font><br>");
                    }
                }
                sb.append("</p>");
                sb.append("</body></html>");

                PrintWriter pw = response.getWriter();
                pw.print(sb.toString());
                response.flushBuffer();
            }
        };

        config.addHandler("/compress", handler);
        Server.start(config);
    }
}
