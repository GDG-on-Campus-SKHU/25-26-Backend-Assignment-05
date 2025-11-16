package com.gdg.oauthgoogleloginexample.controller;

import com.gdg.oauthgoogleloginexample.dto.TokenDto;
import com.gdg.oauthgoogleloginexample.service.GoogleLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/oauth2")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final GoogleLoginService googleLoginService;
    @GetMapping("/callback/google")
    public TokenDto googleCallback(@RequestParam("code") String code) {

        String googleAccessToken = googleLoginService.getGoogleAccessToken(code);

        return googleLoginService.loginOrSignUp(googleAccessToken);
    }
}
