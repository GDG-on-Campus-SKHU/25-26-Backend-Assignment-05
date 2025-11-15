package org.example.oauth.common;

public final class Constants {

    private Constants() {}

    public static final String CONTENT_TYPE = "application/json;charset=utf-8";
    public static final String MESSAGE_INTRO = "{\"message\":\"";
    public static final String MESSAGE_OUTRO = "\"}";
    public static final Long TWO_WEEKS = 14L * 24 * 3600;
    public static final String HEADER_VALUE = "/index.html?loggedIn=google";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
}
