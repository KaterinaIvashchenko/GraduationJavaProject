package ru.ifmo.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.*;

import static ru.ifmo.server.Http.HEADER_NAME_CONTENT_LENGTH;
import static ru.ifmo.server.Http.HEADER_NAME_CONTENT_TYPE;

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
    private List<Cookie> cookies;
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

    private List<Cookie> getCookies() {

        if (getHeaders().get("Cookie") == null) {
            return null;
        }

        if (cookies == null) {
            cookies = new ArrayList<>();
            String cookieline = getHeaders().get("Cookie");
            String[] pairs = cookieline.split("; ");
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split("=");
                cookies.add(new Cookie(keyValue[0], keyValue[1]));
            }
        }
        return Collections.unmodifiableList(cookies);
    }

    public String getCookieValue(String cookiename) {

        if (cookies == null) {
            cookies = getCookies();
        }

        Map<String, String> cookieValues = new HashMap<>();

        if (cookies != null) {
            for (Cookie currentCookie : cookies) {
                cookieValues.put(currentCookie.name, currentCookie.value);
            }
            return cookieValues.get(cookiename);
        } else return null;
    }

    private Boolean containsCookie(String cookiename) {

        Boolean flag = false;

        if (cookies == null) {
            cookies = getCookies();
        }
        if (cookies != null) {
            for (Cookie currentCookie : cookies) {
                if (currentCookie.name.equals("JSESSIONID")) {
                    flag = true; }
            }
        }
        return flag;
    }


    public Session getSession() {
        if (!containsCookie("JSESSIONID")) {
            session = new Session();
            String uniqSid = Session.generateSID();
            session.setId(uniqSid);
            Server.setSessions(uniqSid, session);
        } else {
            session = Server.getSessions().get(getCookieValue("JSESSIONID"));
            if (session == null) {
                session = getSession(true);
            }
        }
        return session;
    }

    public Session getSession(Boolean create) {
            session = new Session();
            String uniqSid = Session.generateSID();
            session.setId(uniqSid);
            Server.setSessions(uniqSid, session);
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
