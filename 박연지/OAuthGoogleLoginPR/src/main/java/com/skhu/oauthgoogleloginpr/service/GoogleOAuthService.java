package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.google.GoogleUserInfo;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleOAuthService {

    private final GoogleClient googleClient;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public TokenDto loginOrSignUp(String code) {
        String googleAccessToken = googleClient.requestAccessToken(code);
        GoogleUserInfo googleUser = googleClient.requestUserInfo(googleAccessToken);

        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> registerGoogleUser(googleUser));

        TokenDto tokens = tokenService.generateTokens(user);
        String savedRefreshToken = tokenService.saveOrUpdateRefreshToken(
                user.getId(), tokens.refreshToken());

        return new TokenDto(tokens.accessToken(), savedRefreshToken);
    }

    private User registerGoogleUser(GoogleUserInfo googleUser) {
        User newUser = User.builder()
                .username(googleUser.getName())
                .email(googleUser.getEmail())
                .password(null)
                .role(Role.USER)
                .build();

        return userRepository.save(newUser);
    }
}
