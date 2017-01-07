package ru.ifmo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.server.annotation.URL;
import ru.ifmo.server.util.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.ifmo.server.util.Utils.htmlMessage;
import static ru.ifmo.server.Http.*;

/**
 * Ifmo Web Server.
 * <p>
 *     To start server use {@link #start(ServerConfig)} and register at least
 *     one handler to process HTTP requests.
 *     Usage example:
 *     <pre>
 *{@code
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
 *     To stop the server use {@link #stop()} or {@link #close()} methods.
 * </p>
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

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private Server(ServerConfig config) {
        this.config = new ServerConfig(config);
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

            server.openConnection();
            server.startAcceptor();

            LOG.info("Server started on port: {}", config.getPort());
            return server;
        }
        catch (IOException e) {
            throw new ServerException("Cannot start server on port: " + config.getPort());
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
        Utils.closeQuiet(socket);

        socket = null;
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
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Malformed URL", e);

            respond(SC_BAD_REQUEST, "Malformed URL", htmlMessage(SC_BAD_REQUEST + " Malformed URL"),
                    sock.getOutputStream());

            return;
        }
        catch (Exception e) {
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

        final String path = dispatcher != null ?  dispatcher.dispatch(req, resp) : req.getPath();

        Handler handler = config.handler(path);
        ServerConfig.ReflectHandler ref = config.getReflectHandler(req.getPath());

        if (handler != null) {
            try {
                handler.handle(req, resp);
            }
            catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.error("Server error:", e);
                respond(SC_SERVER_ERROR,htmlMessage(SC_SERVER_ERROR + " Server error"),resp);
            }
        } else if (ref != null) {
            try {
                int count = 0;
                URL an = ref.m.getAnnotation(URL.class);
                if (an.methods()[0].equals(HttpMethod.ANY)) {
                    ref.m.invoke(ref.obj, req, resp);
                }
                for (int i = 0; i < an.methods().length; i++) {
                    if (req.method.equals(an.methods()[i])) {
                        ref.m.invoke(ref.obj, req, resp);
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    respond(SC_BAD_REQUEST, "Bad Request", htmlMessage(SC_BAD_REQUEST + " The request \""
                            + req.method + "\" has invalid method"), sock.getOutputStream());
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                if (LOG.isDebugEnabled())
                    LOG.error("Method invoke error:", e);
            }
        } else
            respond(SC_NOT_FOUND, "Not Found", htmlMessage(SC_NOT_FOUND + " Not found"),
                    sock.getOutputStream());
    }

    static void respond(int code, String statusMsg, String content, OutputStream out) throws IOException {
        out.write(("HTTP/1.0" + SPACE + code + SPACE + statusMsg + CRLF + CRLF + content).getBytes());
        out.flush();
    }
    static void respond(int code, String content, Response resp) throws IOException {
        resp.setStatusCode(code);
        resp.setBody(content.getBytes());
        resp.flushBuffer();
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
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket sock = socket.accept()) {
                    sock.setSoTimeout(config.getSocketTimeout());

                    processConnection(sock);
                }
                catch (Exception e) {
                    if (!Thread.currentThread().isInterrupted())
                        LOG.error("Error accepting connection", e);
                }
            }
        }
    }
}
