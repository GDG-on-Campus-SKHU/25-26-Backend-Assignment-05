package com.skhu.oauthgoogleloginpr.dto.google;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class GoogleTokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private Long expiresIn;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;
}

