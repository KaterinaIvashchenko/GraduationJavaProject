package ru.ifmo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by GilEO on 11.01.2017.
 */
public class SessionListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try  {

                for (Map.Entry<String, Session> entry : Server.getSessions().entrySet()) {
                    LocalDateTime ltnow = LocalDateTime.now();
                    Thread.currentThread().sleep(1000);
                    if (entry.getValue().getExpire() != null && ltnow.isAfter(entry.getValue().getExpire())) {
                        LOG.info("Deleting session '" + entry.getKey() + "'. Goodbye " + entry.getValue().getParams("name") + " " + entry.getValue().getParams("surname"));
                        Server.removeSession(entry.getKey());
//                        LOG.info("Sessions map after delete: " + Server.getSessions());
                    }
                }
            }
            catch (NullPointerException e) {
                    e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
