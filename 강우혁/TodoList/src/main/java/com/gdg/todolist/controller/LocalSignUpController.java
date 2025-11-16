package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LocalSignupRequestDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.UserInfoResponseDto;
import com.gdg.todolist.service.LocalAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Auth", description = "사용자 인증 API")
public class LocalSignUpController {
    private final LocalAuthService localAuthService;

    @Operation(summary = "관리자 회원가입", description = "관리자 계정을 생성하고 토큰 반환")
    @PostMapping("/admin")
    public ResponseEntity<TokenDto> adminSignup(@RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localAuthService.adminSingUp(localSignupRequestDto));
    }

    @Operation(summary = "일반 회원가입", description = "일반 사용자 계정을 생성하고 토큰 반환")
    @PostMapping("/user")
    public ResponseEntity<TokenDto> userSignup(@RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localAuthService.userSignUp(localSignupRequestDto));
    }

    @Operation(summary = "나의 정보 조회", description = "내 정보를 보여주는 페이지")
    @GetMapping("/getInfo")
    public ResponseEntity<UserInfoResponseDto> getInfo(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getMyInfo(principal));
    }

    @Operation(summary = "관리자 전용 회원 조회", description = "유저 아이디를 확인하여 조회")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.getUserInfo(userId));
    }

    @Operation(summary = "회원정보 수정", description = "유저의 정보 수정")
    @PatchMapping("/update/{userId}")
    public ResponseEntity<UserInfoResponseDto> updateUserInfo(Principal principal, @RequestBody LocalSignupRequestDto localSignupRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(localAuthService.update(principal, localSignupRequestDto));
    }

    @Operation(summary = "회원 삭제", description = "유저 정보를 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<TokenDto> deleteUser(Principal principal) {
        localAuthService.deleteUser(principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
