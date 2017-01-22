package ru.ifmo.server;

import ru.ifmo.server.annotation.URL;

import java.io.IOException;


public class ScanClassFile {

    @URL(method = HttpMethod.GET, value = "/scan")
    public void indexScanClassGET(Request request, Response response) throws IOException {
    }
}
