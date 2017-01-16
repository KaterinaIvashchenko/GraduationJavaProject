package ru.ifmo.server;


public class RequestBody {
    String contentType;
    String bodyTextPlain;
    int contentLength;

    public String getContentType() {
        return contentType;
    }

    public String getBodyTextPlain() {
        return bodyTextPlain;
    }

    public int getContentLength() {
        return contentLength;
    }
}
