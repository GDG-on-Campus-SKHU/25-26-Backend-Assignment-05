package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.RefreshToken;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.global.code.ErrorStatus;
import com.skhu.oauthgoogleloginpr.global.exception.GeneralException;
import com.skhu.oauthgoogleloginpr.global.jwt.TokenProvider;
import com.skhu.oauthgoogleloginpr.repository.RefreshTokenRepository;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenDto generateTokens(User user) {
        String accessToken = tokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = tokenProvider.createRefreshToken(
                user.getId(),
                user.getEmail()
        );

        return new TokenDto(accessToken, refreshToken);
    }

    public String saveOrUpdateRefreshToken(Long userId, String refreshToken) {

        RefreshToken saved = refreshTokenRepository.findByUserId(userId)
                .map(existingToken -> {
                    existingToken.updateToken(
                            refreshToken,
                            LocalDateTime.now().plusDays(7)
                    );
                    return existingToken;
                })
                .orElseGet(() ->
                        refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(userId)
                                        .token(refreshToken)
                                        .expiration(LocalDateTime.now().plusDays(7))
                                        .build()
                        )
                );

        return saved.getToken();
    }

    public void validateRefreshToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_INVALID);
        }
    }

    public TokenDto reissueAccessToken(String refreshToken) {

        // 1) 토큰 자체가 유효한지
        validateRefreshToken(refreshToken);

        // 2) userId 추출
        Long userId = tokenProvider.getUserId(refreshToken);

        // 3) DB에 저장된 refreshToken 조회
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        // 4) 요청된 refreshToken과 DB값 비교
        if (!savedToken.getToken().equals(refreshToken)) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_MISMATCH);
        }

        // 5) refreshToken 만료 여부 확인
        if (savedToken.isExpired()) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        // 6) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 7) 새로운 AccessToken 발급
        String newAccessToken = tokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        // refresh는 그대로 반환
        return new TokenDto(newAccessToken, refreshToken);
    }
}
