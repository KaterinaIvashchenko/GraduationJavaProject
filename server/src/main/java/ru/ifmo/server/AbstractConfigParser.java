package ru.ifmo.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;


public abstract class AbstractConfigParser implements ConfigParser {

    protected final InputStream in;

    public AbstractConfigParser(File file) {

        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ServerException("Cannot find config file", e);
        }
    }

    public AbstractConfigParser(InputStream in) {
        this.in = in;
    }

    protected void reflectiveSetParam(ServerConfig config, String key, String val) throws ReflectiveOperationException {

        String setterName = setterName(key);

        Method[] methods = ServerConfig.class.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();

            if (setterName.equals(name)) {
                Class<?>[] params = method.getParameterTypes();

                assert params.length == 1;

                Class<?> type = toPrimitive(params[0]);

                if (int.class == type) {
                    method.invoke(config, Integer.parseInt(val));

                } else if (short.class == type) {
                    method.invoke(config, Short.parseShort(val));

                } else if (long.class == type) {
                    method.invoke(config, Long.parseLong(val));

                } else if (double.class == type) {
                    method.invoke(config, Double.parseDouble(val));

                } else if (float.class == type) {
                    method.invoke(config, Float.parseFloat(val));

                } else if (char.class == type) {
                    assert val.length() == 1;
                    method.invoke(config, val.charAt(0));

                } else if (boolean.class == type) {
                    method.invoke(config, Boolean.parseBoolean(val));

                } else if (byte.class == type) {
                    method.invoke(config, Byte.parseByte(val));

                } else {

                    Class<?> aClass = Class.forName(val);
                    Object obj = aClass.newInstance();
                    method.invoke(config, obj);

                }
            }
        }
    }

    private Class<?> toPrimitive(Class < ? > cls) {

        if (cls.isPrimitive())
            return cls;

        if (Integer.class == cls)
            return int.class;

        if (Short.class == cls)
            return short.class;

        if (Long.class == cls)
            return long.class;

        if (Double.class == cls)
            return double.class;

        if (Float.class == cls)
            return float.class;

        if (Character.class == cls)
            return char.class;

        if (Boolean.class == cls)
            return boolean.class;

        if (Byte.class == cls)
            return byte.class;

        return cls;
    }

    private String setterName(String key) {
        String setterName = key;
        char letter = setterName.charAt(0);
        letter = Character.toUpperCase(letter);
        setterName = setterName.substring(1);

        return "set" + letter + setterName;
    }
}