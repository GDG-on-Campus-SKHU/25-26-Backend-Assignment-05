package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LocalLoginRequestDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "로컬 로그인", description = "로컬 로그인 지원")
    @GetMapping("/local")
    public ResponseEntity<TokenDto> login(@RequestBody LocalLoginRequestDto loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(loginService.login(loginRequest));
    }
}

