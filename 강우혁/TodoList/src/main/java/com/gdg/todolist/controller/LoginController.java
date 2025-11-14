package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LoginDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/local")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(loginService.login(loginRequest));
    }
}

