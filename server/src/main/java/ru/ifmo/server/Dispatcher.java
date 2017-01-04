package ru.ifmo.server;

/**
 * Define your implementations of this interface and set with
 * {@link ServerConfig#setDispatcher(Dispatcher)}.
 * Method {@link #dispatch(Request, Response)} will be invoked before process
 * request
 * @see ServerConfig
 * @see Server
 */
public interface Dispatcher {
    /**
     * Invoked before process request. Return new path for after handle.
     * @param request Request object keeps request information: method, headers, params
     *                and provides {@link java.io.InputStream} to get additional data
     *                from client.
     * @param response Response object provides {@link java.io.OutputStream} ro respond to
     *                 client.
     * @return return String path for after handle
     */
    String dispatch(Request request, Response response);
}
