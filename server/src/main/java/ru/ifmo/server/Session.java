package ru.ifmo.server;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gil on 08-Jan-17.
 */
public class Session {

    String id;
    LocalDateTime expire;

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(Integer minutes) {
        LocalDateTime expLdt = LocalDateTime.now().plusMinutes(minutes);
        this.expire = expLdt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void invalidate(){
        Server.removeSession(id);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionData=" + sessionData + ", " +
                "expires=" + expire +
                '}';
    }

    private Map<String, Object> sessionData;

    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    public <T> void setParams(String key, T t) {
        if (sessionData == null)
            sessionData = new HashMap<>();
        sessionData.put(key, t);
    }

    public <T> T getParams(String key) {
        if (sessionData != null) {
            return (T) sessionData.get(key);
        } else return null;

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
