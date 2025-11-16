package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.user.LoginRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.global.code.ErrorStatus;
import com.skhu.oauthgoogleloginpr.global.exception.GeneralException;
import com.skhu.oauthgoogleloginpr.repository.RefreshTokenRepository;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 로그인 및 토큰 발급
    public TokenDto login(LoginRequestDto requestDto) {
        User user = authenticateUser(requestDto);
        TokenDto tokens = tokenService.generateTokens(user);
        String savedRefreshToken = tokenService.saveOrUpdateRefreshToken(user.getId(), tokens.refreshToken());
        return new TokenDto(tokens.accessToken(), savedRefreshToken);
    }

    private User authenticateUser(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }
        return user;
    }
}
