package org.example.oauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestToken {
    public static String findRefresh(HttpServletRequest req, String cookieName) {
        Cookie[] cs = req.getCookies();

        if (cs == null) {
            return null;
        }

        for (Cookie c : cs) {
            if (cookieName.equals(c.getName())) return c.getValue();
        }

        return null;
    }
}
