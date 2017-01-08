package ru.ifmo.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static ru.ifmo.server.Http.SC_OK;
import static ru.ifmo.server.Http.statusNames;
import static ru.ifmo.server.Server.CRLF;
import static ru.ifmo.server.Server.SPACE;

/**
 * Provides {@link java.io.OutputStream} ro respond to client.
 */
public class Response {
    final Socket socket;
    private int statusCode;
    private ByteArrayOutputStream bufferOutputStream;
    private PrintWriter printWriter;
    private Map<String, String> headers;
    static List<String> cacheCookies;

    Response(Socket socket) {
        this.socket = socket;
    }

    /**
     * Forces any content in the buffer to be written to the client
     */
    public void flushBuffer() {
        if (statusCode == 0)
            statusCode = SC_OK;

        try {
            if (printWriter != null)
                printWriter.flush();
            bufferOutputStream.flush();

            if ((this.headers == null) || (this.headers.get("Content-Length") == null))
                this.setHeader("Content-Length", String.valueOf(bufferOutputStream.size()));

            OutputStream out = socket.getOutputStream();
            out.write(("HTTP/1.0" + SPACE + statusCode + SPACE + statusNames[statusCode] + CRLF).getBytes());

            for (String key : headers.keySet()) {
                out.write((key + ":" + SPACE + headers.get(key) + CRLF).getBytes());
            }

            if (cacheCookies != null) {
                for (String cookie : cacheCookies) {
                    out.write(("Set-Cookie:" + SPACE + cookie + CRLF).getBytes());
                }
                cacheCookies.clear();
            }

            out.write(CRLF.getBytes());
            out.write(bufferOutputStream.toByteArray());
            out.flush();
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * @return {@link OutputStream} connected to the client.
     */
    @Deprecated
    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * Returns a buffered OutputStream suitable for writing binary data in the response. Need send responseto client exec method FlushBuffer
     *
     * @return buffered OutputStream
     */
    public OutputStream getOutputStreamBuffer() {
        if (bufferOutputStream == null)
            bufferOutputStream = new ByteArrayOutputStream();

        return bufferOutputStream;
    }

    /**
     * Set {@link ru.ifmo.server.Response} body binary data
     *
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
     * flush() calling automatically on flushBuffer()
     *
     * @return {@link PrintWriter}
     * @throws ServerException if an output exception occurred
     */
    public PrintWriter getWriter() {
        if (printWriter == null)
            printWriter = new PrintWriter(getOutputStreamBuffer());
        return printWriter;
    }

    /**
     * Adds a http response header with the given name and value. Header Content-Length set automatically when flushBuffer
     *
     * @param name  name header
     * @param value String value header
     */
    public void setHeader(String name, String value) {
        if (this.headers == null)
            this.headers = new LinkedHashMap<>();

        this.headers.put(name, value);
    }

    /**
     * rewrite http headers with map name and value
     *
     * @param headers map name and value
     */
    public void setHeaders(Map<String, String> headers) {
        if (this.headers == null)
            this.headers = new LinkedHashMap<>();

        this.headers.putAll(headers);
    }

    /**
     * get map http headers with name and value
     *
     * @return Map<String, String> headers
     */
    public Map<String, String> getHeaders() {
        if (this.headers == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(this.headers);
    }

    /**
     * This method sets an arbitrary http status code.
     *
     * @param code method takes an int (the status code) as an argument.
     */
    public void setStatusCode(int code) {
        if ((code < 100) || (code > 505)) {
            throw new ServerException("Not valid http status code:" + code);
        }
        statusCode = code;
    }

    /**
     * Method return current response http status code
     *
     * @return int http status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set header Content-type with value
     *
     * @param value String value Internet Media Types
     */
    public void setContentType(String value) {
        setHeader("Content-Type", value);
    }
}
