package ru.ifmo.server;

import java.io.File;

/**
 * Universal config file parser
 */

public class ConfigLoader {

    public ServerConfig load(File file) {

        return getParser(file).parse();
    }

    public ConfigParser getParser(File file) {
        if (file.getName().endsWith(".properties"))
            return new PropertiesConfigParser(file);

        throw new ServerException("Unsupported format");
    }
}
