package ru.ifmo.server;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by xmitya on 02.01.17.
 */

public interface ConfigParser {

    ServerConfig parse() throws ReflectiveOperationException, IOException, XMLStreamException;
}
