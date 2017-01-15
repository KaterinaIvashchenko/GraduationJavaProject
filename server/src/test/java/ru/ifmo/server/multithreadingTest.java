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

import static java.lang.Thread.sleep;
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
        isfinished = !isfinished;
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


        isfinished = false;

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

        new Thread(new RequestHandler(client1, host, get1)).start(); // На этот запрос стоит тайм-аут в 200 милисекунд
        assertEquals(false, isfinished); // Первый поток еще НЕ закончил свою работу.

        new Thread(new RequestHandler(client2, host, get2)).start(); // На этот запрос тайм-аут отсутствует
        sleep(100); // Если этот тайм-аут сделать меньше или вообще убрать, то поток не успевает отработать
        assertEquals(true, isfinished); // Второй поток закончил свою работу

        sleep(200); // Если этот тайм-аут сделать меньше, то мы получаем ошибку - java.lang.IllegalStateException: Connection pool shut down
                          // отрабатывает метод IOUtils.closeQuietly(client1);
        assertEquals(false, isfinished); // Первый поток закончил свою работу.

    }
}

