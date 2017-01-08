package ru.ifmo.server;

import javax.sound.midi.Soundbank;
import java.util.*;

import static ru.ifmo.server.Response.cacheCookies;

/**
 * Created by Gil on 08-Jan-17.
 */
public class CookieManager {

    private List<Cookie> cookies;
    static Session session;

    public List<Cookie> getCookies() {
        return cookies;
    }

    public List<Cookie> getCookies(Request request) {

        if (request.getHeaders().get("Cookie") == null) {
            return null;
        }

        if (cookies == null)
            cookies = new ArrayList<>();

        String cookieline = request.getHeaders().get("Cookie");
        String[] pairs = cookieline.split("; ");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split("=");
            cookies.add(new Cookie(keyValue[0], keyValue[1]));
        }
        return Collections.unmodifiableList(cookies);
    }

    public String getCookieValue(String cookiename) {

        if (cookies == null) {
            return null;
        }

        Map<String, String> cookieValues = new HashMap<>();

        for (Cookie currentCookie: cookies) {
            cookieValues.put(currentCookie.name, currentCookie.value);
        }

        return cookieValues.get(cookiename);
    }

    public void setCookie(Cookie cookie) {

        if (cacheCookies == null) {
            cacheCookies = new ArrayList<>();
        }

        StringBuilder cookieline = new StringBuilder();

        cookieline.append(cookie.name + "=" + cookie.value);
        if (cookie.maxage != null) cookieline.append(";MAX-AGE=" + cookie.maxage);
        if (cookie.domain != null) cookieline.append(";DOMAIN=" + cookie.domain);
        if (cookie.path != null) cookieline.append(";PATH=" + cookie.path);
        cookieline.append(";");

        cacheCookies.add(cookieline.toString());
    }

    public Session getSession() {
        if (getCookieValue("JSESSIONID") == null) {
            session = new Session();
            String uniqSid = Session.generateSID();
            setCookie(new Cookie("JSESSIONID", uniqSid, "2000", null, null, null));
            session.setId(uniqSid);
            return session;
        } else {
            return session;
        }
    }

}
