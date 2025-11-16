package com.gdg.oauthgoogleloginexample.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class UserInfoDto {

    private String name;
    private String email;

    @SerializedName("verified_email")
    private Boolean verifiedEmail;
}
