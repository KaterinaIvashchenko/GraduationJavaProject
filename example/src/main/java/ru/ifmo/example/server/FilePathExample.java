package ru.ifmo.example.server;

import ru.ifmo.server.*;

import java.io.File;
import java.io.Writer;

/**
 * Created by Adm on 1/27/2017.
 */
public class FilePathExample {
    public static void main(String[] args) {
        ServerConfig config = new ServerConfig()
                .setWorkDir(new File("D:\\JavaDiplomItmo2\\GraduationJavaProject\\example\\src\\main\\resources"));


        Server.start(config);
    }
}
