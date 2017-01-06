package ru.ifmo.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Universal config file parser
 */

public class ConfigLoader {

    public ServerConfig load(File file) {

        try {
            return getParser(file).parse();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServerException("Can't parse config");
        }
    }

    public ServerConfig load() {
        File prop = new File(getClass().getClassLoader().getResource("web-server.properties").getFile());

        //Где нужно искать файл пропертис или xml? В файловой системе или в ресурсах?

        try {
            return getParser(prop).parse();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServerException("Can't parse config");
        }
    }

    public ConfigParser getParser(File file) {
        if (file.getName().endsWith(".properties"))
            return new PropertiesConfigParser(file);

        throw new ServerException("Unsupported format");
    }
}
