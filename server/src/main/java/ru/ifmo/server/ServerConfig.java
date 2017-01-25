package ru.ifmo.server;

import java.util.*;


/**
 * Holds server configs: local port, handler mappings, etc.
 */
public class ServerConfig {
    /** Default local port. */
    public static final int DFLT_PORT = 8080;

    private int port = DFLT_PORT;
    private Map<String, Handler> handlers;
    private Map<String, Class<? extends Handler>> userHandlersClasses;
    private Collection<Class<?>> classes;
    private int socketTimeout;
    private Dispatcher dispatcher;

    public ServerConfig() {
        handlers = new HashMap<>();
        userHandlersClasses = new HashMap<>();
        classes = new HashSet<>();
    }

    public ServerConfig(ServerConfig config) {
        this();

        port = config.port;
        handlers = new HashMap<>(config.handlers);
        userHandlersClasses = new HashMap<>(config.userHandlersClasses);
        classes = new HashSet<>(config.classes);
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

    public ServerConfig addHandlerClass(String path, Class<? extends Handler> MyClass) {
        userHandlersClasses.put(path, MyClass);

        return this;
    }

    public ServerConfig addHandlersClasses(Map<String, Class<? extends Handler>> handlers) {
        userHandlersClasses.putAll(handlers);

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

    public ServerConfig addClasses(Collection<Class<?>> classes) {
        this.classes.addAll(classes);

        return this;
    }

    public void addClass(Class<?> cls) {
        this.classes.add(cls);
    }

    public Collection<Class<?>> getClasses() {
        return classes;
    }

    public Map<String, Class<? extends Handler>> getUserHandlersClasses() {
        return userHandlersClasses;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "port=" + port +
                ", handlers=" + handlers +
                ", classes=" + classes +
                ", socketTimeout=" + socketTimeout +
                ", dispatcher=" + dispatcher +
                '}';
    }

    /**
     * Set request dispatcher.
     *
     * @param dispatcher Dispatcher to set.
     * @return Itself for chaining.
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
}
