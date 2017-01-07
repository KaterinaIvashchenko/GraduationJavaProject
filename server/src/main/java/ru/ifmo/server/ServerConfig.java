package ru.ifmo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.server.annotation.URL;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds server configs: local port, handler mappings, etc.
 */
public class ServerConfig {
    /** Default local port. */
    public static final int DFLT_PORT = 8080;

    private int port = DFLT_PORT;
    private Map<String, Handler> handlers;
    private Map<String, ReflectHandler> classHandlers;
    private int socketTimeout;
    private Dispatcher dispatcher;

    private static final Logger LOG = LoggerFactory.getLogger(ServerConfig.class);

    public ServerConfig() {
        handlers = new HashMap<>();
        classHandlers = new HashMap<>();
    }

    public ServerConfig(ServerConfig config) {
        this();

        port = config.port;
        handlers = new HashMap<>(config.handlers);
        classHandlers = new HashMap<>(config.classHandlers);
        socketTimeout = config.socketTimeout;
        dispatcher = config.dispatcher;
    }

    /**
     * @return Local port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Define local port.
     *
     * @param port TCP port.
     * @return Itself for chaining.
     */
    public ServerConfig setPort(int port) {
        this.port = port;

        return this;
    }

    /**
     * Add handler mapping.
     *
     * @param path Path which will be associated with this handler.
     * @param handler Request handler.
     * @return Itself for chaining.
     */
    public ServerConfig addHandler(String path, Handler handler) {
        handlers.put(path, handler);

        return this;
    }

    /**
     * Add handler mappings.
     *
     * @param handlers Map paths to handlers.
     * @return Itself for chaining.
     */
    public ServerConfig addHandlers(Map<String, Handler> handlers) {
        this.handlers.putAll(handlers);

        return this;
    }

    Handler handler(String path) {
        return handlers.get(path);
    }

    /**
     * @return Current handler mapping.
     */
    public Map<String, Handler> getHandlers() {
        return handlers;
    }

    /**
     * Set handler mappings.
     *
     * @param handlers Handler mappings.
     */
    public void setHandlers(Map<String, Handler> handlers) {
        this.handlers = handlers;
    }

    /**
     * @return Socket timeout value.
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Set socket timeout. By default it's unlimited.
     *
     * @param socketTimeout Socket timeout, 0 means no timeout.
     * @return Itself for chaining.
     */
    public ServerConfig setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;

        return this;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "port=" + port +
                ", handlers=" + handlers +
                ", classHandlers=" +classHandlers +
                ", socketTimeout=" + socketTimeout +
                ", dispatcher=" + dispatcher +
                '}';
    }

    /**
     * Set request dispatcher.
     *
     * @param dispatcher Dispatcher to set.
     * @return Itself for chaining.
     * @see Dispatcher
     */
    public ServerConfig setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    /**
     * Get request dispatcher.
     *
     * @return Request dispatcher or <tt>null</tt> if nothing set.
     */
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    class ReflectHandler {
        Method m;
        Object obj;

        public ReflectHandler(Object obj, Method m) {
            this.m = m;
            this.obj = obj;
        }
    }

    public ServerConfig addScanClass (Class scanClass) {
        try {
            String name = scanClass.getName();
            Class<?> cls = Class.forName(name);
            boolean validParameters = false;

            for (Method method : cls.getDeclaredMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (params[0].equals(Request.class) && params[1].equals(Response.class))
                        validParameters = true;

                if (!validParameters) continue;

                URL an = method.getAnnotation(URL.class);
                if (an != null) {
                    Modifier.isPublic(method.getModifiers());
                    String path = an.value();
                    ReflectHandler reflectHandler = new ReflectHandler(cls.newInstance(), method);
                    classHandlers.put(path, reflectHandler);
                }
            }
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Class not found:" + scanClass, e);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return this;
    }

    ReflectHandler getReflectHandler (String path) {
        return classHandlers.get(path);
    }
}
