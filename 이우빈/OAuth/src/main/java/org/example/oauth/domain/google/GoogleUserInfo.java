package org.example.oauth.domain.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;

    @JsonProperty("verified_email")
    private Boolean verifiedEmail;
}
