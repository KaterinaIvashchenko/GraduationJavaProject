package ru.ifmo.server.scan.fail;


import ru.ifmo.server.HttpMethod;
import ru.ifmo.server.Request;
import ru.ifmo.server.annotation.URL;

public class ScanClassInvalidParameters {
    @URL(method = HttpMethod.GET, value = "/scan")
    public void failParameters(Request request) {
    }
}
