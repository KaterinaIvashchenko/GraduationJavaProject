package ru.ifmo.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keeps request information: method, headers, params
 * and provides {@link java.io.InputStream} to get additional data
 * from client.
 */
public class Request {
    private final Socket socket;
    public HttpMethod method;
    public URI path;
    private String contentType;
    private String bodyTextPlain;
    private int contentLength;

    private Map<String, String> headers;
    private Map<String, String> args;

    public Request(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return {@link InputStream} connected to the client.
     */
    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        }
        catch (IOException e) {
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBodyTextPlain() {
        return bodyTextPlain;
    }

    public void setBodyTextPlain(String bodyTextPlain) {
        this.bodyTextPlain = bodyTextPlain;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public Map<String, String> getHeaders() {
        if (headers == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(headers);
    }

    public void addHeader(String key, String value) {
        if (headers == null)
            headers = new LinkedHashMap<>();

        headers.put(key, value);
    }

    public void addArgument(String key, String value) {
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

    public Map<String, String> getURLEncoded() {
        return getArguments();
    }

    @Override
    public String toString() {
        return "Request{" +
                "socket=" + socket +
                ", method=" + method +
                ", path=" + path +
                ", Content-Type=" + contentType +
                ", Content-Length=" + contentLength +
                ", headers=" + headers +
                ", args=" + args +
                '}';
    }
}
