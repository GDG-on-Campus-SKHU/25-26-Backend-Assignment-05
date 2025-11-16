package com.skhu.oauthgoogleloginpr.global.code;

public interface BaseCode {
    String getCode();
    String getMessage();
    ReasonDTO getReasonHttpStatus();
}
