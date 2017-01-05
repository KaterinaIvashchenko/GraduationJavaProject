package ru.ifmo.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;

/**
 * Provides {@link java.io.OutputStream} ro respond to client.
 */
public class Response {
    final Socket socket;

    Response(Socket socket) {
        this.socket = socket;
    }

    /**
     * Forces any content in the buffer to be written to the client
     */
    public void flushBuffer() {

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
    }

    /**
     * Returns a PrintWriter object that can send character text to the client.
     Calling flush() on the PrintWriter commits the response.
     * @return {@link PrintWriter}
     * @throws ServerException  if an output exception occurred
     */
    public PrintWriter getWriter() {
        try {
            return new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }
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
     * This method sets an arbitrary http status code.
     * @param code method takes an int (the status code) as an argument.
     */
    public void setStatusCode (int code) {

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
