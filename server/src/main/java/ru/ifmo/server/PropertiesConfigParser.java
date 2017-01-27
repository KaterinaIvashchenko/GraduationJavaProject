package ru.ifmo.server;

import ru.ifmo.server.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/** Parser for properties file */

public class PropertiesConfigParser extends AbstractConfigParser {

    public PropertiesConfigParser(File file) {
        super(file);
    }

    public PropertiesConfigParser(InputStream in) {
        super(in);
    }

    @Override
    public ServerConfig parse() throws ReflectiveOperationException, IOException {

        Properties prop = new Properties();

        try {
            prop.load(in);
        }
        finally {
            Utils.closeQuiet(in);
        }

        Enumeration<String> keys = (Enumeration<String>) prop.propertyNames();
        ServerConfig config = new ServerConfig();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String val = (String) prop.get(key);

            if ("handlers".equals(key)) {

                String[] mapping = val.split(",");

                for (String route: mapping) {

                    String[] split = route.split("=");

                    Handler handler = (Handler) Class.forName(split[1]).newInstance();
                    config.addHandler(split[0], handler);
                }

            } else if ("scanclass".equals(key)) {

                String[] mapping = val.split(",");

                for (String route : mapping) {
                    config.addClass(Class.forName(route));
                }
            }

            else {

                reflectiveSetParam(config, key, val);

            }
        }

        return config;
    }
}
