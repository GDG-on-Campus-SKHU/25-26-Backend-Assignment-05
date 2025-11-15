package org.example.oauth.service;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.user.RefreshToken;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.jwt.TokenProvider;
import org.example.oauth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto saveAndReturnToken(Long userId, String role) {
        String accessToken = tokenProvider.createAccessToken(userId, role);
        String refreshToken = tokenProvider.createRefreshToken(userId);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(rt -> {
                            rt.updateRefreshToken(refreshToken);
                        },
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(userId)
                                        .token(refreshToken)
                                        .build()
                        ));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
