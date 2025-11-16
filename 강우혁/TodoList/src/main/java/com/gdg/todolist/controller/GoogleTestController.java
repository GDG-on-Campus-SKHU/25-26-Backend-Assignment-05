package com.gdg.todolist.controller;

import com.gdg.todolist.domain.User;
import com.gdg.todolist.service.GoogleLoginService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class GoogleTestController {
    private final GoogleLoginService googleLoginService;

    @Operation(summary = "구글 로그인", description = "소셜 로그인을 지원")
    @GetMapping("/google")
    public User login(Principal principal) {
        return googleLoginService.googleLogin(principal);
    }
}
