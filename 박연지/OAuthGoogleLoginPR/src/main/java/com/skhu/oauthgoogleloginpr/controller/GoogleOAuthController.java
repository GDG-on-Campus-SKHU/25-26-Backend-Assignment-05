package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/callback/google")
    public ResponseEntity<TokenDto> googleCallback(@RequestParam("code") String code) {
        return ResponseEntity.ok(googleOAuthService.loginOrSignUp(code));
    }
}
