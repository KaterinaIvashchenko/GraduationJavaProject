package ru.ifmo.server;

/*
 * Define your implementations of this interface and register with
 * {@link ServerConfig#addHandler(String, Handler)}
 * or {@link ServerConfig#addHandlers(Map)}.
 * Method {@link #handle(Request, Response)} will be invoked on each
 * request according to mapping in {@link ServerConfig}.
 * @see ServerConfig
 * @see Server
 */

public interface Dispatcher {
    public String dispatch(Request request, Response response);

}
