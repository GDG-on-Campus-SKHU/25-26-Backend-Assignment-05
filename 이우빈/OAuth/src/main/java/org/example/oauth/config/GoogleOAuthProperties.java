package org.example.oauth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String scope;
}
