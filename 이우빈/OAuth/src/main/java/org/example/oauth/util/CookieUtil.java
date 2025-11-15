package org.example.oauth.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public final class CookieUtil {
    private CookieUtil(){}

    public static final String PATH_PRIMARY = "/auth/refresh";
    public static final String PATH_FALLBACK = "/";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String SAME_SITE = "Lax";
    private static final Long MAX_AGE_SECONDS = 0L;

    private static String build(String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(false)
                .sameSite(SAME_SITE)
                .path(path)
                .maxAge(maxAgeSeconds)
                .build().toString();
    }

    public static String setRefreshCookie(String token, long maxAgeSec) {
        return build(token, maxAgeSec, PATH_PRIMARY);
    }

    public static String expireAt(String path) {
        return build("", MAX_AGE_SECONDS, path);
    }

    public static void rotateRefreshCookies(HttpHeaders headers, String newRefreshToken, long maxAgeSec) {
        headers.add(HttpHeaders.SET_COOKIE, expireAt(PATH_PRIMARY));
        headers.add(HttpHeaders.SET_COOKIE, expireAt(PATH_FALLBACK));
        headers.add(HttpHeaders.SET_COOKIE, setRefreshCookie(newRefreshToken, maxAgeSec));
    }

    public static String[] expireAllRefreshCookies() {
        return new String[] {
                expireAt(PATH_PRIMARY),
                expireAt(PATH_FALLBACK)
        };
    }
}
