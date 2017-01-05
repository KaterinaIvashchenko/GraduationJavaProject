package ru.ifmo.server;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;

import static ru.ifmo.server.Http.statusNames;

/**
 * Provides {@link java.io.OutputStream} ro respond to client.
 */
public class Response {
    final Socket socket;
    private int statusCode;
    private ByteArrayOutputStream bufferOutputStream= new ByteArrayOutputStream();

    Response(Socket socket) {
        this.socket = socket;
    }

    /**
     * Forces any content in the buffer to be written to the client
     */
    public void flushBuffer() {
        if (statusCode==0)
            throw new ServerException("Not set http status code");

        try {
            socket.getOutputStream().write(("HTTP/1.0 "+statusCode+" "+statusNames[statusCode]+"\r\n\r\n").getBytes());
            socket.getOutputStream().write(bufferOutputStream.toByteArray());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * @return {@link OutputStream} connected to the client.
     */
    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        }
        catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * Set {@link ru.ifmo.server.Response} body binary data
     * @param data byte array to set body response
     */
    public void setBody(byte[] data) {
        try {
            bufferOutputStream.write(data);
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
    }

    /**
     * Returns a PrintWriter object that can send character text to the client.
     Calling flush() on the PrintWriter commits the response.
     * @return {@link PrintWriter}
     * @throws ServerException  if an output exception occurred
     */
    public PrintWriter getWriter() {
        return new PrintWriter(bufferOutputStream);
    }

    /**
     * Adds a http response header with the given name and value
     * @param name name header
     * @param value String value header
     */
    public void setHeader(String name, String value) {
    }

    /**
     * rewrite http headers with map name and value
     * @param headers map name and value
     */
    public void setHeaders (Map<String, String> headers) {
    }

    /**
     * get map http headers with name and value
     * @return Map<String, String> headers
     */
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
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
        return 0;
    }

/*
+Response.setBody(byte[] data): void
+Response.getWriter(): Writer
Reponse.getOutputStream(): OutputStream // but it should not send directly to client
+Response.setHeader(String name, String value): void
Response.getHeaders(): Map<String, String>
+Response.setHeaders(Map<String, String> headers): void
+Response.setStatusCode(int code): void
+Response.getStatusCode(): int
*/


}
