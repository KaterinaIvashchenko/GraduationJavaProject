package ru.ifmo.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;

/** Parser for properties file */

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
            String key = keys.nextElement();
            String val = (String) prop.get(key);

            if ("handlers".equals(key)) {

                String[] mapping = val.split(",");

                for (String route: mapping) {

                    String[] split = route.split("=");

                    try {
                        Handler handler = (Handler) Class.forName(split[1]).newInstance();
                        config.addHandler(split[0], handler);

                    } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                        throw new ServerException("Cannot load object for handler " + split[1], e);
                    }
                }

            } else {

                String setterName = key;
                char letter = setterName.charAt(0);
                letter = Character.toUpperCase(letter);
                setterName = setterName.substring(1);
                setterName = "set" + letter + setterName;

                Method[] methods = ServerConfig.class.getDeclaredMethods();
                for (Method method : methods) {
                    String name = method.getName();

                    if (setterName.equals(name)) {
                        Class<?>[] params = method.getParameterTypes();

                        assert params.length == 1;

                        if (int.class == params[0]) {
                            int valConverted = Integer.parseInt(val);

                        } else if (Integer.class == params[0]) {
                            Integer valConverted = Integer.parseInt(val);

                        } else if (Short.class == params[0]) {
                            Short valConverted = Short.parseShort(val);
                        } else if (short.class == params[0]) {
                            short valConverted = Short.parseShort(val);

                        } else if (Long.class == params[0]) {
                            Long valConverted = Long.parseLong(val);
                        } else if (long.class == params[0]) {
                            long valConverted = Long.parseLong(val);

                        } else if (Double.class == params[0]) {
                            Double valConverted = Double.parseDouble(val);
                        } else if (double.class == params[0]) {
                            double valConverted = Double.parseDouble(val);

                        } else if (Float.class == params[0]) {
                            Float valConverted = Float.parseFloat(val);
                        } else if (float.class == params[0]) {
                            float valConverted = Float.parseFloat(val);

                        } else if (Character.class == params[0]) {
                            Character valConverted = val.charAt(0);
                        } else if (char.class == params[0]) {
                            char valConverted = val.charAt(0);

                        } else if (Boolean.class == params[0]) {
                            Boolean valConverted = Boolean.parseBoolean(val);
                        } else if (boolean.class == params[0]) {
                            boolean valConverted = Boolean.parseBoolean(val);

                        } else if (Byte.class == params[0]) {
                            Byte valConverted = Byte.parseByte(val);
                        } else if (byte.class == params[0]) {
                            byte valConverted = Byte.parseByte(val);

                            /** Необходимо разобраться, как элегантно оборачивать каждый method.invoke трайкэчем
                            с одними и теми же эксепшинами для всех прим. типов данных и их оберток!

                            try {
                                method.invoke(config, valConverted);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }

                            */

                        } else {

                            Class<?> aClass = null;
                            try {
                                aClass = Class.forName(val);
                                Object obj = null;
                                obj = aClass.newInstance();
                                method.invoke(config, obj);
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return config;
    }
}
