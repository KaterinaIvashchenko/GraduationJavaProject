package ru.ifmo.server.scan.fail;

import ru.ifmo.server.HttpMethod;
import ru.ifmo.server.Request;
import ru.ifmo.server.Response;
import ru.ifmo.server.annotation.URL;


public class ScanClassInvalidModifier {
    @URL(method = HttpMethod.GET, value = "/scan")
    private void failParameters(Request request, Response response) {
    }
}
