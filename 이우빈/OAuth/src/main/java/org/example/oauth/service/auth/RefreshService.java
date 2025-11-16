package org.example.oauth.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.refresh.response.RefreshResponse;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.exception.BadRequestException;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.util.CookieUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final AuthService authService;

    private static final long ROTATE_BEFORE_MS = Duration.ofDays(3).toMillis();
    private static final long NEW_REFRESH_MAX_AGE = Duration.ofDays(7).getSeconds();

    public RefreshResponse refreshAndRotate(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        TokenDto result = authService.refresh(refreshToken, ROTATE_BEFORE_MS);

        String setCookie = (result.getRefreshToken() != null)
                ? CookieUtil.setRefreshCookie(result.getRefreshToken(), NEW_REFRESH_MAX_AGE)
                : null;

        return new RefreshResponse(result.getAccessToken(), setCookie);
    }
}

