package com.skhu.oauthgoogleloginpr.dto.google;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class GoogleUserInfo {
    private String id;
    private String email;

    @SerializedName("verified_email")
    private boolean verifiedEmail;

    private String name;

    @SerializedName("picture")
    private String pictureUrl;
}

