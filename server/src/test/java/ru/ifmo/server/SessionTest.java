package ru.ifmo.server;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by GilEO on 26.01.2017.
 */
public class SessionTest {

    @Test
    public void testParams() throws IOException {
        Session session = new Session();
        session.setParam("name", "Vasya");
        assertEquals("Vasya", session.getParam("name"));
    }

    @Test(expected = SessionException.class)
    public void testExpired() throws IOException {
        Session session = new Session();
        session.setExpired(true);
        session.setParam("name", "Vasya");
        session.getParam("name");
    }

    @Test
    public void testLiveTime() throws IOException {

        int min = 1;

        Session session = new Session();
        session.setExpire(min);
        assertEquals(session.getExpire(), LocalDateTime.now().plusMinutes(min));
    }
}
