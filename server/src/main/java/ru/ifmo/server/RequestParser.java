package ru.ifmo.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import static ru.ifmo.server.Http.*;
import static ru.ifmo.server.HttpMethod.*;
import static ru.ifmo.server.Server.respond;
import static ru.ifmo.server.util.Utils.htmlMessage;

class RequestParser {

    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final char HEADER_VALUE_SEPARATOR = ':';
    private static final char SPACE = ' ';
    private static final char AMP = '&';
    private static final char EQ = '=';
    private static final int READER_BUF_SIZE = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(RequestParser.class);

    static Request parseRequest(Socket socket) throws IOException, URISyntaxException {
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

        if (req.method == POST || req.method == PUT) {

            if (req.getBody().getContentType() == null) {
                respond(SC_BAD_REQUEST, "Bad Request", htmlMessage(SC_BAD_REQUEST + " The request \""
                        + req.method + "\" has no Content-Type"), socket.getOutputStream());
                return null;
            }

            if (req.getBody().getContentLength() == 0) {
                respond(SC_BAD_REQUEST, "Bad Request", htmlMessage(SC_BAD_REQUEST + " The request \""
                        + req.method + "\" has no Content"), socket.getOutputStream());
                return null;
            }
        }

        readBody(reader, sb, req);

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

        req.addHeader(key, sb.substring(start, len).trim());

        if (key.equals("Cookie")) {
            String[] pairs = sb.substring(start, len).trim().split("; ");
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split("=");
                req.insertCookie(keyValue[0], keyValue[1]);
            }
        }
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

    private static void parseURLEncoded(Request req, StringBuilder sb) throws IOException {
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

        if (key != null) {
            req.addArgument(key, null);
        }
    }

    // TODO support binary content
    private static void readBody(InputStreamReader in, StringBuilder sb, Request req) throws IOException {
        final RequestBody body = req.getBody();

        if (body.contentLength == 0)
            return;

        int c;
        int count = 0;
        while ((c = in.read()) >= 0) {
            sb.append((char) c);
            count++;
            if (count == body.getContentLength()) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Read content: {}", sb.toString());

                break;
            }
        }

        if (MIME_TEXT_PLAIN.equals(body.contentType))
            body.bodyTextPlain = sb.toString();

        else if (MIME_URL_ENCODED.equals(body.contentType))
            parseURLEncoded(req, sb);
    }
}
