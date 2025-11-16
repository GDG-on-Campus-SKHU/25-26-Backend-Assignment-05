package org.example.oauth.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.user.RefreshToken;
import org.example.oauth.domain.user.User;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.exception.BadRequestException;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.jwt.TokenProvider;
import org.example.oauth.repository.RefreshTokenRepository;
import org.example.oauth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
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

    public TokenDto validateAndRotate(String refreshToken, long rotateBeforeMs) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_TOKEN));

        if (!refreshToken.equals(stored.getToken())) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        String newAccess = tokenProvider.createAccessToken(user.getId(), user.getRole().name());

        long expirationTime = tokenProvider.parseClaim(refreshToken).getExpiration().getTime();
        long remainingTime = expirationTime - System.currentTimeMillis();

        if (remainingTime <= rotateBeforeMs) {
            String newRefresh = tokenProvider.createRefreshToken(user.getId());
            stored.updateRefreshToken(newRefresh);

            return TokenDto.builder()
                    .accessToken(newAccess)
                    .refreshToken(newRefresh)
                    .build();
        }

        return TokenDto.builder()
                .accessToken(newAccess)
                .refreshToken(null)
                .build();
    }
}
