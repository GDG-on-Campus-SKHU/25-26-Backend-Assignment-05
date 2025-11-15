package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LocalUserInfoDto;
import com.gdg.todolist.dto.LocalUserSignUpDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.service.LocalAuthService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LocalSignUpController {
    private final LocalAuthService localAuthService;

    @PostMapping("/sign/admin")
    public ResponseEntity<TokenDto> adminSignUp(@RequestBody LocalUserSignUpDto localUserSignUpDto) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.adminSignUp(localUserSignUpDto));
    }

    @PostMapping("/sign/user")
    public ResponseEntity<TokenDto> signUp(@RequestBody LocalUserSignUpDto localUserSignUpDto) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.userSignUp(localUserSignUpDto));
    }

    @GetMapping("/info")
    public ResponseEntity<LocalUserInfoDto> info(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getMyInfo(principal));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<LocalUserInfoDto> getInfo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getUserInfo(id));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<LocalUserInfoDto> updateInfo(@PathVariable Long id, @RequestBody LocalUserSignUpDto localUserSignUpDto) {
        return ResponseEntity.ok(localAuthService.update(id, localUserSignUpDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<LocalUserInfoDto> deleteUserInfo(Principal principal) {
        localAuthService.deleteUser(principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
