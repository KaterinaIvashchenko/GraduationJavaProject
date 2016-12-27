package ru.ifmo.server.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.server.HttpMethod;
import ru.ifmo.server.Request;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ParseRequest {

    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final char HEADER_VALUE_SEPARATOR = ':';
    private static final char SPACE = ' ';
    private static final char AMP = '&';
    private static final char EQ = '=';
    private static final int READER_BUF_SIZE = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(ParseRequest.class);

    public static Request parseRequest(Socket socket) throws IOException, URISyntaxException {
        InputStreamReader reader = new InputStreamReader(socket.getInputStream());

        Request req = new Request(socket);
        StringBuilder sb = new StringBuilder(READER_BUF_SIZE); // TODO

        while (readLine(reader, sb) > 0) {
            if (req.method == null)
                parseRequestLine(req, sb);
            else
                parseHeader(req, sb);

            sb.setLength(0);
        }

        if (req.getContentType() != null) {
            if (req.getContentType().equals("text/plain")) {
                readBody(reader, sb, req);
                req.setBodyTextPlain(sb.toString());

            } else if (req.getContentType().equals("application/x-www-form-urlencoded")) {
                readBody(reader, sb, req);
                readBodyURLEncoded(req, sb);
            }
        }

        return req;
    }

    private static int readLine(InputStreamReader in, StringBuilder sb) throws IOException {
        int c;
        int count = 0;

        while ((c = in.read()) >= 0) {
            if (c == LF)
                break;

            sb.append((char) c);

            count++;
        }

        if (count > 0 && sb.charAt(count - 1) == CR)
            sb.setLength(--count);

        if (LOG.isTraceEnabled())
            LOG.trace("Read line: {}", sb.toString());

        return count;
    }

    private static void parseHeader(Request req, StringBuilder sb) {
        String key = null;

        int len = sb.length();
        int start = 0;

        for (int i = 0; i < len; i++) {
            if (sb.charAt(i) == HEADER_VALUE_SEPARATOR) {
                key = sb.substring(start, i).trim();

                start = i + 1;

                break;
            }
        }

        String value = sb.substring(start, len).trim();

        if (key != null) {
            if (key.equals("Content-Type")) {
                req.setContentType(value);
            }
        }

        if (key != null) {
            if (key.equals("Content-Length")) {
                req.setContentLength(Integer.parseInt(value));
            }
        }

        req.addHeader(key, value);
    }

    private static void parseRequestLine(Request req, StringBuilder sb) throws URISyntaxException {
        int start = 0;
        int len = sb.length();

        for (int i = 0; i < len; i++) {
            if (sb.charAt(i) == SPACE) {
                if (req.method == null)
                    req.method = HttpMethod.valueOf(sb.substring(start, i));
                else if (req.path == null) {
                    req.path = new URI(sb.substring(start, i));

                    break; // Ignore protocol for now
                }

                start = i + 1;
            }
        }

        assert req.method != null : "Request method can't be null";
        assert req.path != null : "Request path can't be null";

        String query = req.path.getQuery();

        if (query != null) {
            start = 0;

            String key = null;

            for (int i = 0; i < query.length(); i++) {
                boolean last = i == query.length() - 1;

                if (key == null && query.charAt(i) == EQ) {
                    key = query.substring(start, i);

                    start = i + 1;
                }
                else if (key != null && (query.charAt(i) == AMP || last)) {
                    req.addArgument(key, query.substring(start, last ? i + 1 : i));

                    key = null;
                    start = i + 1;
                }
            }

            if (key != null)
                req.addArgument(key, null);
        }
    }

    private static void readBodyURLEncoded(Request req, StringBuilder sb) throws IOException {
        int start = 0;

        String key = null;

        for (int i = 0; i < sb.length(); i++) {
            boolean last = i == sb.length() - 1;

            if (key == null && sb.charAt(i) == EQ) {
                key = sb.substring(start, i);

                start = i + 1;
            }
            else if (key != null && (sb.charAt(i) == AMP || last)) {
                req.addArgument(key, sb.substring(start, last ? i + 1 : i));

                key = null;
                start = i + 1;
            }
        }

        if (key != null)
            req.addArgument(key, null);
    }

    private static void readBody(InputStreamReader in, StringBuilder sb, Request req) throws IOException {
        int c;
        int count = 0;
        while ((c = in.read()) >= 0) {
            sb.append((char) c);
            count++;
            if (count == req.getContentLength()) break;
        }
    }
}
