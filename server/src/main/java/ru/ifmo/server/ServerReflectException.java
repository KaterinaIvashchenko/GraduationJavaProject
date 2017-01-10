package ru.ifmo.server;


public class ServerReflectException extends RuntimeException {
    public ServerReflectException(String message) {
        super(message);
    }

    public ServerReflectException(String message, Throwable cause) {
        super(message, cause);
    }
}
