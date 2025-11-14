package com.gdg.todolist.controller;

import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.service.GoogleLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;

    @GetMapping("/callback/google")
    public TokenDto callback(@RequestParam("code") String code) {

        String token = googleLoginService.getGoogleAccessToken(code);

        return googleLoginService.loginOrSingUp(token);
    }
}
