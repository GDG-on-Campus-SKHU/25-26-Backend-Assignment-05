package org.example.oauth.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.oauth.config.GoogleOAuthProperties;
import org.example.oauth.domain.google.GoogleTokenResponse;
import org.example.oauth.domain.google.GoogleUserInfo;
import org.example.oauth.domain.user.Provider;
import org.example.oauth.domain.user.Role;
import org.example.oauth.domain.user.User;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.exception.BadRequestException;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleOAuthProperties googleOAuthProperties;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    private final RestTemplate restTemplate;

    private static final String AUTH_BASE = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    public String buildAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(AUTH_BASE)
                .queryParam("client_id", googleOAuthProperties.getClientId())
                .queryParam("redirect_uri", googleOAuthProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", googleOAuthProperties.getScope())
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }

    @Transactional
    public TokenDto loginWithCode(String code) {
        String googleAccessToken = exchangeCodeForAccessToken(code);
        GoogleUserInfo profile = fetchUserInfo(googleAccessToken);

        if (profile.getEmail() == null || Boolean.FALSE.equals(profile.getVerifiedEmail())) {
            throw new BadRequestException(ErrorMessage.OAUTH_EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findByEmail(profile.getEmail())
                .map(u -> {
                    if (u.getProvider() != Provider.GOOGLE) {
                        throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER);
                    }

                    if (u.getProviderId() == null) {
                        u = User.builder()
                                .id(u.getId())
                                .name(profile.getName())
                                .email(u.getEmail())
                                .role(u.getRole())
                                .provider(Provider.GOOGLE)
                                .providerId(profile.getId())
                                .build();

                        userRepository.save(u);
                    }
                    return u;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(profile.getName())
                                .email(profile.getEmail())
                                .role(Role.ROLE_USER)
                                .provider(Provider.GOOGLE)
                                .providerId(profile.getId())
                                .build()
                ));

        return tokenService.saveAndReturnToken(user.getId(), user.getRole().name());
    }

    private String exchangeCodeForAccessToken(String code) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", googleOAuthProperties.getClientId());
        form.add("client_secret", googleOAuthProperties.getClientSecret());
        form.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        form.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, httpHeaders);
        ResponseEntity<GoogleTokenResponse> responseEntity = restTemplate.postForEntity(TOKEN_URL, entity, GoogleTokenResponse.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null || responseEntity.getBody().getAccessToken() == null) {
            throw new BadRequestException(ErrorMessage.OAUTH_CODE_EXCHANGE_FAILED);
        }

        return responseEntity.getBody().getAccessToken();
    }

    private GoogleUserInfo fetchUserInfo(String accessToken) {
        URI uri = UriComponentsBuilder.fromUriString(USERINFO_URL).build(true).toUri();

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        ResponseEntity<GoogleUserInfo> responseEntity = restTemplate.exchange(requestEntity, GoogleUserInfo.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new BadRequestException(ErrorMessage.OAUTH_PROFILE_FETCH_FAILED);
        }
        return responseEntity.getBody();
    }
}
