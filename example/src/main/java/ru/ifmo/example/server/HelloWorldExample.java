package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Simple hello world example.
 */
public class HelloWorldExample {
    public static void main(String[] args) {

        ServerConfig config = new ServerConfig();

        config.setWorkDir(new File("D:\\JavaDiplomItmo2\\GraduationJavaProject\\example\\src\\main\\resources"));

        Server.start(config);
    }
}
