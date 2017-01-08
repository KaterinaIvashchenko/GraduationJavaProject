package ru.ifmo.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gil on 08-Jan-17.
 */
public class Session {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionData=" + sessionData +
                '}';
    }

    private Map<String, Object> sessionData;

    public <T> void setParams(String key, T t) {
        if (sessionData == null)
            sessionData = new HashMap<>();
        sessionData.put(key, t);
    }

    public <T> T getParams(String key) {
        return (T) sessionData.get(key);
    }

    public static String generateSID() {
        String symbols = "abcdefghijklmnopqrstuvwxyz123456789";

        StringBuilder randString = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
        }

        return randString.toString();
    }
}
