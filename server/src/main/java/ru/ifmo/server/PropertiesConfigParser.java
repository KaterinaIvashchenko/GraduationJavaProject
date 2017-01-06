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
    public ServerConfig parse() throws IllegalAccessException, InvocationTargetException {

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

                        //Избыточность кода зашкаливает. Чем можно навести красоту?

                        if (int.class == params[0]) {
                            int valConverted = Integer.parseInt(val);
                            method.invoke(config, valConverted);

                        } else if (Integer.class == params[0]) {
                            Integer valConverted = Integer.parseInt(val);
                            method.invoke(config, valConverted);

                        } else if (Short.class == params[0]) {
                            Short valConverted = Short.parseShort(val);
                            method.invoke(config, valConverted);
                        } else if (short.class == params[0]) {
                            short valConverted = Short.parseShort(val);
                            method.invoke(config, valConverted);

                        } else if (Long.class == params[0]) {
                            Long valConverted = Long.parseLong(val);
                            method.invoke(config, valConverted);
                        } else if (long.class == params[0]) {
                            long valConverted = Long.parseLong(val);
                            method.invoke(config, valConverted);

                        } else if (Double.class == params[0]) {
                            Double valConverted = Double.parseDouble(val);
                            method.invoke(config, valConverted);
                        } else if (double.class == params[0]) {
                            double valConverted = Double.parseDouble(val);
                            method.invoke(config, valConverted);

                        } else if (Float.class == params[0]) {
                            Float valConverted = Float.parseFloat(val);
                            method.invoke(config, valConverted);
                        } else if (float.class == params[0]) {
                            float valConverted = Float.parseFloat(val);
                            method.invoke(config, valConverted);

                        } else if (Character.class == params[0]) {
                            Character valConverted = val.charAt(0);
                            method.invoke(config, valConverted);
                        } else if (char.class == params[0]) {
                            char valConverted = val.charAt(0);
                            method.invoke(config, valConverted);

                        } else if (Boolean.class == params[0]) {
                            Boolean valConverted = Boolean.parseBoolean(val);
                            method.invoke(config, valConverted);
                        } else if (boolean.class == params[0]) {
                            boolean valConverted = Boolean.parseBoolean(val);
                            method.invoke(config, valConverted);

                        } else if (Byte.class == params[0]) {
                            Byte valConverted = Byte.parseByte(val);
                            method.invoke(config, valConverted);
                        } else if (byte.class == params[0]) {
                            byte valConverted = Byte.parseByte(val);
                            method.invoke(config, valConverted);

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
