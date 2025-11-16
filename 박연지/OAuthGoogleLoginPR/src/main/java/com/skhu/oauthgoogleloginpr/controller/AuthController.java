package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.user.LoginRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.RefreshTokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.SignupRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.UserInfoResponseDto;
import com.skhu.oauthgoogleloginpr.service.AuthService;
import com.skhu.oauthgoogleloginpr.service.TokenService;
import com.skhu.oauthgoogleloginpr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signup(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody RefreshTokenDto requestDto) {
        return ResponseEntity.ok(tokenService.reissueAccessToken(requestDto.refreshToken()));
    }
}
