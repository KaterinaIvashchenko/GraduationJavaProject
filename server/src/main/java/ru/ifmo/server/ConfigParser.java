package ru.ifmo.server;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by xmitya on 02.01.17.
 */

public interface ConfigParser {

    ServerConfig parse() throws IllegalAccessException, InvocationTargetException;
}
