package com.skhu.oauthgoogleloginpr.global.exception;

import com.skhu.oauthgoogleloginpr.global.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseCode code;

    public GeneralException(BaseCode code, Throwable cause) {
        super(code.getReasonHttpStatus().getMessage(), cause);
        this.code = code;
    }
}
