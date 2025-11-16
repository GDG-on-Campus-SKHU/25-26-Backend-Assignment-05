package com.example.oauthgoogleloginexample.service;

import com.example.oauthgoogleloginexample.config.GoogleOAuthProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
public class GoogleOAuthService {
    private static final String GOOGLE_ACCOUNTS_ISSUER = "https://accounts.google.com";

    private final String clientId;
    private final GoogleIdTokenVerifier verifier;

    public GoogleOAuthService(GoogleOAuthProperties properties) {
        if (!StringUtils.hasText(properties.getClientId())) {
            throw new IllegalStateException("Google OAuth client id must be configured");
        }

        this.clientId = properties.getClientId();

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .setIssuer(GOOGLE_ACCOUNTS_ISSUER)
                .build();
    }

    public GoogleUser verifyIdToken(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new IllegalArgumentException("Google account email must be verified");
            }

            String email = payload.getEmail();
            if (!StringUtils.hasText(email)) {
                throw new IllegalArgumentException("Google account email is required");
            }

            return new GoogleUser(
                    payload.getSubject(),
                    email,
                    (String) payload.getOrDefault("name", ""),
                    (String) payload.getOrDefault("picture", "")
            );
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to verify Google ID token", e);
            throw new IllegalStateException("Failed to verify Google ID token", e);
        }
    }

    public record GoogleUser(String subject, String email, String name, String pictureUrl) {
    }
}
