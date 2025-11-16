package com.gdg.todolist.service;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.domain.User;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.GoogleUserInfoDto;
import com.gdg.todolist.exception.BadReqeustException;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    @Value("${jwt.google-client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${jwt.google-client-pw}")
    private String GOOGLE_CLIENT_PW;
    private final String GOOGLE_REDIRECT_URI = "http://localhost:8080/api/oauth2/callback/google";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_PW);
        params.add("redirect_uri", GOOGLE_REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, TokenDto.class)
                    .getAccessToken();
        }

        throw new BadReqeustException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    public TokenDto loginOrSingUp(String getGoogleAccessToken) {
        GoogleUserInfoDto googleUserInfoDto = getUserInfo(getGoogleAccessToken);

        if(!googleUserInfoDto.getVerifiedEmail()){
            throw new UserNotFoundException("유저 정보가 없습니다.");
        }

        User user = userRepository.findByEmail(googleUserInfoDto.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(googleUserInfoDto.getEmail())
                        .name(googleUserInfoDto.getName())
                        .pictureUrl(googleUserInfoDto.getPictureUrl())
                        .role(Role.ROLE_USER)
                        .provider(Provider.GOOGLE)
                        .build()
                ));

        userRepository.save(user);

        return tokenProvider.createToken(user);
    }

    private GoogleUserInfoDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, GoogleUserInfoDto.class);
        }

        throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
    }

    public User googleLogin(Principal principal) {
        Long id = Long.parseLong(principal.getName());

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }

}
