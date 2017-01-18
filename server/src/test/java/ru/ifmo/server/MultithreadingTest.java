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

public class MultithreadingTest {

    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static final Object monitor = new Object();

    private static void waitMonitor() {
        synchronized (monitor){
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyMonitor() {
        synchronized (monitor){
                monitor.notify();
        }
    }

    private static final String SUCCESS_URL1 = "/test_success1";
    private static final String SUCCESS_URL2 = "/test_success2";

    private static final HttpGet get1 = new HttpGet(SUCCESS_URL1);
    private static final HttpGet get2 = new HttpGet(SUCCESS_URL2);

    private static Server server;
    private static CloseableHttpClient client1;
    private static CloseableHttpClient client2;

    static volatile boolean isFinishedClient1 = false;
    static volatile boolean isFinishedClient2 = false;


    public static void isFinishedClient1True() {
        isFinishedClient1 = true;
    }

    public static void isFinishedClient2True() {
        isFinishedClient2 = true;
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
        IOUtils.closeQuietly(client1);
        IOUtils.closeQuietly(client2);


        server = null;
        client1 = null;
        client2 = null;

        isFinishedClient1 = false;
        isFinishedClient2 = false;


    }

    public class RequestHandler implements Runnable {
        CloseableHttpClient client;
        HttpHost host;
        HttpGet get;

        public RequestHandler(CloseableHttpClient client, HttpHost host, HttpGet get) {
            this.client = client;
            this.host = host;
            this.get = get;
        }

        public void run() {

            try {
                this.client.execute(this.host, this.get);
            } catch (IOException e) {
                System.out.println("Error trying to process request..");
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testProcessConnection() throws Exception {

        new Thread(new RequestHandler(client1, host, get1)).start();

        new RequestHandler(client2, host, get2).run();

        assertEquals(false, isFinishedClient1);
        assertEquals(true, isFinishedClient2);

        waitMonitor();

        assertEquals(true, isFinishedClient1);
        assertEquals(true, isFinishedClient2);

    }
}


