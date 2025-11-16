package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LocalSignupRequestDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.UserInfoResponseDto;
import com.gdg.todolist.service.LocalAuthService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LocalSignUpController {
    private final LocalAuthService localAuthService;

    @PostMapping("/admin")
    public ResponseEntity<TokenDto> adminSignup(@RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localAuthService.adminSingUp(localSignupRequestDto));
    }

    @PostMapping("/user")
    public ResponseEntity<TokenDto> userSignup(@RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localAuthService.userSignUp(localSignupRequestDto));
    }

    @GetMapping("/getInfo")
    public ResponseEntity<UserInfoResponseDto> getInfo(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getMyInfo(principal));
    }

    @GetMapping("/userInfo/{id}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getUserInfo(id));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserInfoResponseDto> updateUserInfo(@PathVariable Long id, @RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.update(id,localSignupRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<TokenDto> deleteUser(Principal principal) {
        localAuthService.deleteUser(principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
