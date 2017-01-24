package ru.ifmo.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static ru.ifmo.server.Http.HEADER_NAME_CONTENT_TYPE;

/**
 * Provides {@link java.io.OutputStream} ro respond to client.
 */
public class Response {
    final Socket socket;
    int statusCode;
    ByteArrayOutputStream bufferOutputStream;
    PrintWriter printWriter;
    Map<String,String> headers = new LinkedHashMap<>();
    List<Cookie> setCookies;

    Response(Socket socket) {
        this.socket = socket;
    }

    public void setCookie(Cookie cookie) {

        if (setCookies == null) {
            setCookies = new ArrayList<>();
        }
        setCookies.add(cookie);
    }

    public void resetCookie(Cookie cookie) {

        if (setCookies == null) {
            setCookies = new ArrayList<>();
        }

        cookie.value = " ";

        setCookies.add(cookie);
    }

    /**
     * Returns a buffered OutputStream suitable for writing binary data in the response. Need send responseto client exec method FlushBuffer
     * @return buffered ByteArrayOutputStream
     */
    public ByteArrayOutputStream getOutputStreamBuffer() {
        if (bufferOutputStream==null)
            bufferOutputStream = new ByteArrayOutputStream();

        return bufferOutputStream;
    }

    /**
     * Set {@link ru.ifmo.server.Response} body binary data
     * @param data byte array to set body response
     */
    public void setBody(byte[] data) {
        try {
            getOutputStreamBuffer().write(data);
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * Returns a PrintWriter object that can send character text to the client.
     flush() calling automatically on flushBuffer()
     * @return {@link PrintWriter}
     * @throws ServerException  if an output exception occurred
     */
    public PrintWriter getWriter() {
        if (printWriter==null)
            printWriter = new PrintWriter(getOutputStreamBuffer());
        return printWriter;
    }

    /**
     * Adds a http response header with the given name and value. Header Content-Length set automatically when flushBuffer
     * @param name name header
     * @param value String value header
     */
    public void setHeader(String name, String value) {
        this.headers.put(name,value);
    }

    /**
     * rewrite http headers with map name and value
     * @param headers map name and value
     */
    public void setHeaders (Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * get map http headers with name and value
     * @return Map<String, String> headers
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    /**
     * This method sets an arbitrary http status code.
     * @param code method takes an int (the status code) as an argument.
     */
    public void setStatusCode (int code) {
        if ( (code<100)||(code>505)  ){
            throw new ServerException("Not valid http status code:" + code);
        }
        statusCode = code;
    }

    /**
     * Method return current response http status code
     * @return int http status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set header Content-type with value
     * @param value String value Internet Media Types
     */
    public void setContentType(String value) {
        setHeader(HEADER_NAME_CONTENT_TYPE,value);
    }
}
