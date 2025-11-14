package com.gdg.todolist.controller;

import com.gdg.todolist.domain.User;
import com.gdg.todolist.service.GoogleLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/google")
@RequiredArgsConstructor
public class GoogleTestController {
    private final GoogleLoginService googleLoginService;

    @GetMapping("/login")
    public User login(Principal principal) {
        return googleLoginService.googleLogin(principal);
    }
}
