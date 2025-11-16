package com.skhu.oauthgoogleloginpr.service;

import com.google.gson.Gson;
import com.skhu.oauthgoogleloginpr.dto.google.GoogleTokenResponse;
import com.skhu.oauthgoogleloginpr.dto.google.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.oauth.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.oauth.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${spring.oauth.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    private final Gson gson = new Gson();

    public String requestAccessToken(String code) {

        Map<String, String> params = Map.of(
                "code", code,
                "client_id", GOOGLE_CLIENT_ID,
                "client_secret", GOOGLE_CLIENT_SECRET,
                "redirect_uri", GOOGLE_REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                GOOGLE_TOKEN_URL,
                params,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 Access Token 요청 실패");
        }

        GoogleTokenResponse tokenResponse =
                gson.fromJson(response.getBody(), GoogleTokenResponse.class);

        return tokenResponse.getAccessToken();
    }

    public GoogleUserInfo requestUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = URI.create(GOOGLE_USERINFO_URL + "?access_token=" + accessToken);

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, uri);

        ResponseEntity<String> response = restTemplate.exchange(
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("유저 정보 요청 실패");
        }

        return gson.fromJson(response.getBody(), GoogleUserInfo.class);
    }
}
