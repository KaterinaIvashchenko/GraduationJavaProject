package ru.ifmo.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.*;

import static ru.ifmo.server.Http.HEADER_NAME_CONTENT_LENGTH;
import static ru.ifmo.server.Http.HEADER_NAME_CONTENT_TYPE;
import static ru.ifmo.server.Session.SESSION_COOKIENAME;
import static ru.ifmo.server.Session.SESSION_LIVETIME;

/**
 * Keeps request information: method, headers, params
 * and provides {@link java.io.InputStream} to get additional data
 * from client.
 */
public class Request {
    private final Socket socket;
    HttpMethod method;
    URI path;

    private RequestBody body;
    private Map<String, String> headers;
    private Map<String, String> args;
    private Map<String, String> cookies;

    private Session session;

    public Request(Socket socket) {
        this.socket = socket;
        body = new RequestBody();
    }

    /**
     * @return {@link InputStream} connected to the client.
     */
    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new ServerException("Unable retrieve input stream.", e);
        }
    }

    /**
     * @return HTTP method of this request.
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * @return Request path.
     */
    public String getPath() {
        return path.getPath();
    }

    /**
     * @return Request body.
     */
    public RequestBody getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        if (headers == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(headers);
    }

    void addHeader(String key, String value) {
        if (headers == null)
            headers = new LinkedHashMap<>();

        if (HEADER_NAME_CONTENT_TYPE.equals(key))
            body.contentType = value;

        else if (HEADER_NAME_CONTENT_LENGTH.equals(key))
            body.contentLength = Integer.parseInt(value);

        headers.put(key, value);
    }

    void addArgument(String key, String value) {
        if (args == null)
            args = new LinkedHashMap<>();

        args.put(key, value);
    }

    /**
     * @return Arguments passed to this request.
     */
    public Map<String, String> getArguments() {
        if (args == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(args);
    }

    void insertCookie(String name, String value) {
        if (cookies == null) {
            cookies = new HashMap<>();
        }
        cookies.put(name, value);
    }

    public Map<String, String> getCookies() {

        if (getHeaders().get("Cookie") == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(cookies);
    }

    public String getCookieValue(String cookiename) {
        return getCookies().get(cookiename);
    }

    private boolean containsJSIDCookie() {
        return getCookies().containsKey(SESSION_COOKIENAME);
    }

    public Session getSession() {
        if (session == null) {
            session = getSession(false);
        }
        return session;
    }

    public Session getSession(boolean create) {
        if (!containsJSIDCookie() || create) {
            session = new Session();
            Server.setSessions(session.getId(), session);
        } else {
            session = Server.getSessions().get(getCookieValue(SESSION_COOKIENAME));
            if (session == null) {
                session = getSession(true);
            }
        }
        return session;
    }


    @Override
    public String toString() {
        return "Request{" +
                "socket=" + socket +
                ", method=" + method +
                ", path=" + path +
                ", Content-Type=" + body.getContentType() +
                ", Content-Length=" + body.getContentLength() +
                ", headers=" + headers +
                ", args=" + args +
                '}';
    }
}
