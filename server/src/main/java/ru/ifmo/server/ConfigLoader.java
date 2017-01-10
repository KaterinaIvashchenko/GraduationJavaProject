package ru.ifmo.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/** Universal config file parser */

public class ConfigLoader {

    public ServerConfig load(File file) {
        assert file != null;

        try {
            return getParser(file).parse();
        } catch (ReflectiveOperationException | IOException e) {
            throw new ServerException("Unable to parse config files: " + file.getAbsolutePath(), e);
        }
    }

    public ServerConfig load() {
       ConfigParser parser = getParser();

        try {
            return parser == null ? null : parser.parse();
        } catch (ReflectiveOperationException | IOException e) {
            throw new ServerException("Unable to parse config files: ", e);
        }
    }

    public ConfigParser getParser(File file) {
        if (file.getName().endsWith(".properties"))
            return new PropertiesConfigParser(file);

        throw new ServerException("Unsupported file format");
    }

    public  ConfigParser getParser() {
        InputStream in = getClass().getClassLoader().getResourceAsStream("web-server.properties");

        if (in != null)
            return new PropertiesConfigParser(in);

        return null;
    }

}
