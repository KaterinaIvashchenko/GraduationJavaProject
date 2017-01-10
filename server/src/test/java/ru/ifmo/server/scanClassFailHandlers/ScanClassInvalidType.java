package ru.ifmo.server.scanClassFailHandlers;

import ru.ifmo.server.HttpMethod;
import ru.ifmo.server.Request;
import ru.ifmo.server.Response;
import ru.ifmo.server.annotation.URL;


public class ScanClassInvalidType {
    @URL(methods = HttpMethod.GET, value = "/scan")
    public String failType(Request request, Response response) {
        return "";
    }
}
