package ru.ifmo.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static ru.ifmo.server.TestUtils.assertStatusCode;

/**
 * Created by Sony on 07.01.2017.
 */
public class multithreadingTest {

    private static final HttpHost host = new HttpHost("localhost", ServerConfig.DFLT_PORT);

    private static final String SUCCESS_URL1 = "/test_success1";
    private static final String SUCCESS_URL2 = "/test_success2";


    private static Server server;
    private static CloseableHttpClient client1; //Юудем имитируем работу 4-ёх клиентов
    private static CloseableHttpClient client2;
    private static CloseableHttpClient client3;
    private static CloseableHttpClient client4;

    static volatile int countOfFinishersThreads = 0;

    public static int getCountOfFinishersThreads() {
        return countOfFinishersThreads;
    }

    public static void incrementCountOfFinishersThreads() {
        countOfFinishersThreads++;
    }


    @BeforeClass
    public static void initialize() {
        ServerConfig cfg = new ServerConfig()
                .addHandler(SUCCESS_URL1, new SuccessHandler1())
                .addHandler(SUCCESS_URL2, new SuccessHandler2());


        server = Server.start(cfg);
        client1 = HttpClients.createDefault();
        client2 = HttpClients.createDefault();
        client3 = HttpClients.createDefault();
        client4 = HttpClients.createDefault();

    }

    @AfterClass
    public static void stop() {
        IOUtils.closeQuietly(server);
        IOUtils.closeQuietly(client1);
        IOUtils.closeQuietly(client2);
        IOUtils.closeQuietly(client3);
        IOUtils.closeQuietly(client4);


        server = null;
        client1 = null;
        client2 = null;
        client3 = null;
        client4 = null;

        countOfFinishersThreads = 0;

    }

    @Test(timeout = 30000)
    public void testprocessConnection() throws Exception {


        HttpGet get1 = new HttpGet(SUCCESS_URL1);
        HttpGet get2 = new HttpGet(SUCCESS_URL2);


/*        CloseableHttpResponse response1 = client1.execute(host, get1);   // Эти тесты проходят
        assertEquals(1, getCountOfFinishersThreads());

        CloseableHttpResponse response2 = client2.execute(host, get2);
        assertEquals(2, getCountOfFinishersThreads());

        CloseableHttpResponse response3 = client3.execute(host, get1);
        assertEquals(3, getCountOfFinishersThreads());

        CloseableHttpResponse response4 = client4.execute(host, get2);
        assertEquals(4, getCountOfFinishersThreads());*/


        CloseableHttpResponse response1 = client1.execute(host, get1);
        CloseableHttpResponse response2 = client2.execute(host, get2);
        CloseableHttpResponse response3 = client3.execute(host, get1);
        CloseableHttpResponse response4 = client4.execute(host, get2);

        // Эти тесты не прохоят! Я запустил 4 потока: 2 из них без тайм-аута и 2 с тайм-аутом в 5 секунд

        assertEquals(2, getCountOfFinishersThreads());  // На данном этапе значение переменной уже равно 4! expected:<2> but was:<4>
        sleep(10000);
        assertEquals(4, getCountOfFinishersThreads());  // До этого теста дело даже не доходит


        assertStatusCode(HttpStatus.SC_OK, response1);
        assertStatusCode(HttpStatus.SC_OK, response2);
        assertStatusCode(HttpStatus.SC_OK, response3);
        assertStatusCode(HttpStatus.SC_OK, response4);


        assertEquals(SuccessHandler1.TEST_RESPONSE + SuccessHandler1.CLOSE_HTML, EntityUtils.toString(response1.getEntity()));
        assertEquals(SuccessHandler2.TEST_RESPONSE + SuccessHandler2.CLOSE_HTML, EntityUtils.toString(response2.getEntity()));
        assertEquals(SuccessHandler1.TEST_RESPONSE + SuccessHandler1.CLOSE_HTML, EntityUtils.toString(response3.getEntity()));
        assertEquals(SuccessHandler2.TEST_RESPONSE + SuccessHandler2.CLOSE_HTML, EntityUtils.toString(response4.getEntity()));


    }
}

