
package ru.ifmo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.server.annotation.URL;
import ru.ifmo.server.util.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.ifmo.server.Http.*;
import static ru.ifmo.server.Session.SESSION_COOKIENAME;
import static ru.ifmo.server.util.Utils.htmlMessage;

/**
 * Ifmo Web Server.
 * <p>
 * To start server use {@link #start(ServerConfig)} and register at least
 * one handler to process HTTP requests.
 * Usage example:
 * <pre>
 * {@code
 * ServerConfig config = new ServerConfig()
 *      .addHandler("/index", new Handler() {
 *          public void handle(Request request, Response response) throws Exception {
 *              Writer writer = new OutputStreamWriter(response.getOutputStream());
 *              writer.write(Http.OK_HEADER + "Hello World!");
 *              writer.flush();
 *          }
 *      });
 *
 * Server server = Server.start(config);
 *      }
 *     </pre>
 * </p>
 * <p>
 * To stop the server use {@link #stop()} or {@link #close()} methods.
 * </p>
 *
 * @see ServerConfig
 */
public class Server implements Closeable {
    private static final char LF = '\n';
    private static final char CR = '\r';
    public static final String CRLF = "" + CR + LF;
    public static final char SPACE = ' ';

    private final ServerConfig config;

    private ServerSocket socket;

    private ExecutorService acceptorPool;
    private ExecutorService connectionProcessingPool;
    private Thread lisThread;

    private static Map<String, Session> sessions = new ConcurrentHashMap<>();

    private Map<String, ReflectHandler> classHandlers;

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private Server(ServerConfig config) {
        this.config = new ServerConfig(config);
        classHandlers = new HashMap<>();
    }

    static Map<String, Session> getSessions() {
        return sessions;
    }

    static void setSessions(String key, Session session) {
        Server.sessions.put(key, session);
    }

    static void removeSession(String key) {
        Server.sessions.remove(key);
    }

    private void listenSessions() throws IOException {
        SessionListener sessionListener = new SessionListener();
        lisThread = new Thread(sessionListener);
        lisThread.start();

        LOG.info("Session listener started, deleting by timeout.");
    }

    public static Server start() {
        return start(new ConfigLoader().load());
    }

    public static Server start(File file) {
        return start(new ConfigLoader().load(file));
    }

    /**
     * Starts server according to config. If null passed
     * defaults will be used.
     *
     * @param config Server config or null.
     * @return Server instance.
     * @see ServerConfig
     */
    public static Server start(ServerConfig config) {
        if (config == null)
            config = new ServerConfig();

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Starting server with config: {}", config);

            Server server = new Server(config);

            server.addScanClasses(config.getClasses());

            server.openConnection();
            server.startAcceptor();

            LOG.info("Server started on port: {}", config.getPort());

            server.listenSessions();

            return server;
        } catch (IOException e) {
            throw new ServerException("Cannot start server on port: " + config.getPort());
        }
    }

    /**
     * Forces any content in the buffer to be written to the client
     */
    public static void flushResponse(Request request, Response response) {
        int statusCode = response.getStatusCode();

        if (statusCode == 0)
            statusCode = SC_OK;
        response.setStatusCode(statusCode);

        try {
            if (response.printWriter != null)
                response.printWriter.flush();

            int contentLength = 0;
            if (response.bufferOutputStream != null) {
                response.bufferOutputStream.flush();
                contentLength = response.bufferOutputStream.size();
            }
            if ((response.headers.get(HEADER_NAME_CONTENT_LENGTH) == null))
                response.setHeader(HEADER_NAME_CONTENT_LENGTH, String.valueOf(contentLength));

            OutputStream out = response.socket.getOutputStream();
            out.write(("HTTP/1.0" + SPACE + statusCode + SPACE + statusNames[statusCode] + CRLF).getBytes());

            for (String key : response.headers.keySet()) {
                out.write((key + ":" + SPACE + response.headers.get(key) + CRLF).getBytes());
            }

            if (request.getSession() != null) {
                response.setCookie(new Cookie(SESSION_COOKIENAME, request.getSession().getId()));
            }

            if (response.setCookies != null) {

                for (Cookie cookie : response.setCookies) {

                    StringBuilder cookieline = new StringBuilder();

                    cookieline.append(cookie.name + "=" + cookie.value);
                    if (cookie.maxage != null) cookieline.append(";MAX-AGE=" + cookie.maxage);
                    if (cookie.domain != null) cookieline.append(";DOMAIN=" + cookie.domain);
                    if (cookie.path != null) cookieline.append(";PATH=" + cookie.path);

                    out.write(("Set-Cookie:" + SPACE + cookieline.toString() + CRLF).getBytes());

                }
            }

            out.write(CRLF.getBytes());
            if (response.bufferOutputStream != null)
                out.write(response.bufferOutputStream.toByteArray());

            out.flush();
        } catch (IOException e) {
            throw new ServerException("Cannot get output stream", e);
        }

    }

    private void openConnection() throws IOException {
        socket = new ServerSocket(config.getPort());
    }

    private void startAcceptor() {
        acceptorPool = Executors.newSingleThreadExecutor(new ServerThreadFactory("con-acceptor"));

        acceptorPool.submit(new ConnectionHandler());
    }

    /**
     * Stops the server.
     */
    public void stop() {
        acceptorPool.shutdownNow();
        connectionProcessingPool.shutdownNow();
        lisThread.interrupt();

        Utils.closeQuiet(socket);

        socket = null;
    }

    private class ReflectHandler {
        Method m;
        Object obj;
        EnumSet<HttpMethod> set;

        ReflectHandler(Object obj, Method m, EnumSet<HttpMethod> set) {
            assert m != null;
            assert obj != null;
            assert set != null && !set.isEmpty();

            this.m = m;
            this.obj = obj;
            this.set = set;
        }

        boolean isApplicable(HttpMethod method) {
            return set.contains(HttpMethod.ANY) || set.contains(method);
        }
    }

    private void addScanClasses(Collection<Class<?>> classes) {
        Collection<Class<?>> classList = new ArrayList<>(classes);

        for (Class<?> c : classList) {
            try {
                String name = c.getName();
                Class<?> cls = Class.forName(name);

                for (Method method : cls.getDeclaredMethods()) {
                    URL an = method.getAnnotation(URL.class);
                    if (an != null) {
                        Class<?>[] params = method.getParameterTypes();
                        Class<?> methodType = method.getReturnType();

                        if (params.length == 2 && methodType.equals(void.class) && Modifier.isPublic(method.getModifiers())
                                && params[0].equals(Request.class) && params[1].equals(Response.class)) {
                            String path = an.value();

                            EnumSet<HttpMethod> set = EnumSet.copyOf(Arrays.asList(an.method()));

                            ReflectHandler reflectHandler = new ReflectHandler(cls.newInstance(), method, set);
                            classHandlers.put(path, reflectHandler);
                        } else {
                            throw new ServerException("Invalid @URL annotated method: " + c.getSimpleName() + "." + method.getName() + "(). "
                                    + "Valid method: must be public void and accept only two arguments: Request and Response." + '\n' +
                                    "Example: public void helloWorld(Request request, Response Response");
                        }

                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new ServerException("Unable initialize @URL annotated handlers. ", e);
            }
        }
    }

    private void processReflectHandler(ReflectHandler rf, Request req, Response resp, Socket sock) throws IOException {
        try {
            rf.m.invoke(rf.obj, req, resp);
            flushResponse(req, resp);
        } catch (Exception e) { // Handle any user exception here.
            if (LOG.isDebugEnabled())
                LOG.error("Error invoke method:" + rf.m, e);

            respond(SC_SERVER_ERROR, "Server Error", htmlMessage(SC_SERVER_ERROR + " Server error"),
                    sock.getOutputStream());
        }
    }

    private void processConnection(Socket sock) throws IOException {
        if (LOG.isDebugEnabled())
            LOG.debug("Accepting connection on: {}", sock);

        Request req;

        try {
            req = RequestParser.parseRequest(sock);

            if (req == null) return;

            if (LOG.isDebugEnabled())
                LOG.debug("Parsed request: {}", req);
        } catch (URISyntaxException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Malformed URL", e);

            respond(SC_BAD_REQUEST, "Malformed URL", htmlMessage(SC_BAD_REQUEST + " Malformed URL"),
                    sock.getOutputStream());

            return;
        } catch (Exception e) {
            LOG.error("Error parsing request", e);

            respond(SC_SERVER_ERROR, "Server Error", htmlMessage(SC_SERVER_ERROR + " Server error"),
                    sock.getOutputStream());

            return;
        }

        if (!isMethodSupported(req.method)) {
            respond(SC_NOT_IMPLEMENTED, "Not Implemented", htmlMessage(SC_NOT_IMPLEMENTED + " Method \""
                    + req.method + "\" is not supported"), sock.getOutputStream());

            return;
        }

        Response resp = new Response(sock);

        Dispatcher dispatcher = config.getDispatcher();

        final String path = dispatcher != null ? dispatcher.dispatch(req, resp) : req.getPath();

        Handler handler = config.handler(path);

        if (handler != null) {
            try {
                handler.handle(req, resp);
                flushResponse(req, resp);
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.error("Server error:", e);
                respond(SC_SERVER_ERROR, htmlMessage(SC_SERVER_ERROR + " Server error"), req, resp);
            }
        } else {
            ReflectHandler reflectHandler = classHandlers.get(req.getPath());
            if (reflectHandler != null && reflectHandler.isApplicable(req.method))
                processReflectHandler(reflectHandler, req, resp, sock);
            else
                respond(SC_NOT_FOUND, "Not Found", htmlMessage(SC_NOT_FOUND + " Not found"),
                        sock.getOutputStream());
        }
    }

    static void respond(int code, String statusMsg, String content, OutputStream out) throws IOException {
        out.write(("HTTP/1.0" + SPACE + code + SPACE + statusMsg + CRLF + CRLF + content).getBytes());
        out.flush();
    }

    static void respond(int code, String content, Request req, Response resp) throws IOException {
        resp.setStatusCode(code);
        resp.setBody(content.getBytes());
        flushResponse(req, resp);
    }

    /**
     * Invokes {@link #stop()}. Usable in try-with-resources.
     *
     * @throws IOException Should be never thrown.
     */
    public void close() throws IOException {
        stop();
    }

    private boolean isMethodSupported(HttpMethod method) {
        switch (method) {
            case GET:
            case DELETE:
            case HEAD:
            case PUT:
            case POST:
                return true;

            default:
                return false;
        }
    }

    private class ConnectionHandler implements Runnable {
        public void run() {
            connectionProcessingPool = Executors.newCachedThreadPool();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket sock = socket.accept();
                    sock.setSoTimeout(config.getSocketTimeout());
                    connectionProcessingPool.submit(new NewConnection(sock));

                } catch (Exception e) {
                    if (!Thread.currentThread().isInterrupted())
                        LOG.error("Error accepting connection", e);
                }
            }

        }
    }

    private class NewConnection implements Runnable {
        Socket sock;

        NewConnection(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    processConnection(sock);
                } catch (IOException e) {
                    LOG.error("Error input / output during data transfer", e);

                } finally {
                    try {
                        sock.close();
                        Thread.currentThread().interrupt();
                    } catch (IOException e) {
                        if (!Thread.currentThread().isInterrupted())
                            LOG.error("Error accepting connection", e);
                        LOG.error("Error closing the socket", e);
                    }
                }
            }
        }
    }
}
