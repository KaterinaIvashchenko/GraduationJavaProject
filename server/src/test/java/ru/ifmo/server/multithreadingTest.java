package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class multithreadingTest {

    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static final String SUCCESS_URL1 = "/test_success1";
    private static final String SUCCESS_URL2 = "/test_success2";

    private static final HttpGet get1 = new HttpGet(SUCCESS_URL1);
    private static final HttpGet get2 = new HttpGet(SUCCESS_URL2);

    private static Server server;
    private static CloseableHttpClient client1;
    private static CloseableHttpClient client2;

    static volatile boolean isfinished = false;

    public synchronized static void isFinishedTrue() {
        isfinished = true;
    }


    @BeforeClass
    public static void initialize() {
        ServerConfig cfg = new ServerConfig()
                .addHandler(SUCCESS_URL1, new SuccessHandler1())
                .addHandler(SUCCESS_URL2, new SuccessHandler2());


        server = Server.start(cfg);
        client1 = HttpClients.createDefault();
        client2 = HttpClients.createDefault();


    }

    @AfterClass
    public static void stop() {
        IOUtils.closeQuietly(server);

        server = null;

        isfinished = false;

    }

    public class RequestHandler {

        CloseableHttpClient client;
        HttpHost host;
        HttpGet get;

        public RequestHandler(CloseableHttpClient client, HttpHost host, HttpGet get) {
            this.client = client;
            this.host = host;
            this.get = get;
        }
    }


    public class Waiter implements Runnable {

        private RequestHandler requestHandler;

        public Waiter(RequestHandler rh) {
            this.requestHandler = rh;
        }

        @Override
        public void run() {
            synchronized (requestHandler) {
                try {
                    requestHandler.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals(true, isfinished);

            }
        }
    }

    public class Notifier implements Runnable {

        private RequestHandler requestHandler;

        public Notifier(RequestHandler rh) {
            this.requestHandler = rh;
        }

        @Override
        public void run() {

            synchronized (requestHandler) {
                try {
                    this.requestHandler.client.execute(this.requestHandler.host, this.requestHandler.get);
                    Thread.currentThread().sleep(200);  // Если уменьшить этот параметр, то запрос не успевает отработать. (накладные расходы на
                                                              // добавления задачи в pool, а так же дальнеёшая её обработка. )
                    requestHandler.notify();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    @Test
    public void testProcessConnection() throws Exception {

        RequestHandler requestHandler1 = new RequestHandler(client1, host, get1);
        RequestHandler requestHandler2 = new RequestHandler(client2, host, get2);

        Waiter waiter1 = new Waiter(requestHandler1);
        new Thread(waiter1, "waiter1").start();

        Waiter waiter2 = new Waiter(requestHandler2);
        new Thread(waiter2, "waiter2").start();

        Notifier notifier1 = new Notifier(requestHandler1);
        new Thread(notifier1, "notifier1").start();

        Notifier notifier2 = new Notifier(requestHandler2);
        new Thread(notifier2, "notifier2").start();

    }
}


