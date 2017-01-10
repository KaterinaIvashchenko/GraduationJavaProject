package ru.ifmo.server.scanClassFailHandlers;


import ru.ifmo.server.HttpMethod;
import ru.ifmo.server.Request;
import ru.ifmo.server.annotation.URL;

public class ScanClassInvalidParameters {
    @URL(methods = HttpMethod.GET, value = "/scan")
    public void failParameters(Request request) {
    }
}
