package ru.ifmo.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by xmitya on 02.01.17.
 */
public class PropertiesConfigParser implements ConfigParser {
    private final File file;

    public PropertiesConfigParser(File file) {
        this.file = file;
    }

    @Override
    public ServerConfig parse() {
        /*
         port=8081
         socketTimeout=5000
         handlers=/index=ru.ifmo.IndexHandler,/login=ru.ifmo.LoginHandler
         */


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

            if ("port".equals(key)) {
                config.setPort(Integer.parseInt(val));
            }
            else if ("socketTimeout".equals(key)) {
                config.setSocketTimeout(Integer.parseInt(val));
            }
            else if ("handlers".equals(key)) {
//                String mapping = "/index=ru.ifmo.IndexHandler";
//
//                String[] split = mapping.split("=");
//
//                split[0] = "/index";
//                split[1] = "ru.ifmo.IndexHandler";
//
//                Class<? extends Handler> cls;
//
//                try {
//                    cls = (Class<? extends Handler>) Class.forName(split[1]);
//                }
//                catch (ClassNotFoundException e) {
//                    throw new ServerException("Cannot parser " + file.getAbsolutePath(), e);
//                }
//
//                config.addHandlerClass(split[0], cls);

            }
        }

        return config;
    }
}
