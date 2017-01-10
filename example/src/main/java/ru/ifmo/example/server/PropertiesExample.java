package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Created by katerina on 1/8/17.
 */

public class PropertiesExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        /*Server server = Server.start(getClass().getClassLoader().getResource("web-server.properties").getFile());
        */
        String infoPage = "http://localhost:" + 8081 + "/info.html";

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(infoPage));
        }
        else
            System.out.println(">>> Open " + infoPage);

    }
}
