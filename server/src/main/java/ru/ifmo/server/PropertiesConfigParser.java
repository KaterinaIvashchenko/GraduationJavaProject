package ru.ifmo.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Parser for properties file
 */

public class PropertiesConfigParser implements ConfigParser {

    private final File file;

    public PropertiesConfigParser(File file) {
        this.file = file;
    }

    @Override
    public ServerConfig parse() {

        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(file));
        }
        catch (IOException e) {
            throw new ServerException("Cannot load file " + file, e);
        }

        Enumeration<String> keys = (Enumeration<String>) prop.propertyNames();
        ServerConfig config = new ServerConfig();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement(); // handlers

            String val = (String) prop.get(key); // /index=ru.ifmo.IndexHandler , /login=ru.ifmo.LoginHandler

            Method[] methods = ServerConfig.class.getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();

            }

            if ("port".equals(key)) {
                config.setPort(Integer.parseInt(val));
            }
            else if ("socketTimeout".equals(key)) {
                config.setSocketTimeout(Integer.parseInt(val));
            }
            else if ("handlers".equals(key)) {

                String[] mapping = val.split(",");

                for (String route: mapping) {

                    String[] split = route.split("=");

                    Class<? extends Handler> cls;

                    try {
                        cls = (Class<? extends Handler>) Class.forName(split[1]);
                    }
                    catch (ClassNotFoundException e) {
                        throw new ServerException("Cannot load object for handler " + split[1], e);
                    }

                    config.addHandlerClass(split[0], cls);

                }
            }
        }

        return config;
    }
}
