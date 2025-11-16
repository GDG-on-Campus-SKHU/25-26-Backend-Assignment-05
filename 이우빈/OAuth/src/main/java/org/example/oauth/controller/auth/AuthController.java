package org.example.oauth.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.oauth.common.Constants;
import org.example.oauth.domain.refresh.response.RefreshResponse;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.dto.user.request.LoginRequest;
import org.example.oauth.dto.user.request.SignUpRequest;
import org.example.oauth.dto.user.response.UserResponse;
import org.example.oauth.service.auth.AuthService;
import org.example.oauth.service.auth.RefreshService;
import org.example.oauth.util.CookieUtil;
import org.example.oauth.util.UserValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshService refreshService;

    private static final String HEADER_NAME = "Set-Cookie";

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenDto token = authService.login(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        CookieUtil.rotateRefreshCookies(headers, token.getRefreshToken(), Constants.TWO_WEEKS);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> myInfo() {
        Long userId = UserValidator.requireLogin();
        return ResponseEntity.ok(authService.myInfo(userId));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
        authService.logout(httpServletRequest);

        return ResponseEntity.noContent()
                .header(HEADER_NAME, CookieUtil.expireAllRefreshCookies())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(
            @CookieValue(value = Constants.REFRESH_TOKEN, required = false) String refreshToken)
    {
        RefreshResponse refreshResponse = refreshService.refreshAndRotate(refreshToken);

        return ResponseEntity.ok()
                .headers(httpHeaders -> {
                    if (refreshResponse.setCookie() != null) {
                        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshResponse.setCookie());
                    }
                })
                .body(TokenDto.builder()
                        .accessToken(refreshResponse.accessToken())
                        .build()
                );
    }
}
