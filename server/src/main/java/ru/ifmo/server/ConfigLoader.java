package ru.ifmo.server;

import java.io.File;

/**
 * Created by xmitya on 02.01.17.
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
