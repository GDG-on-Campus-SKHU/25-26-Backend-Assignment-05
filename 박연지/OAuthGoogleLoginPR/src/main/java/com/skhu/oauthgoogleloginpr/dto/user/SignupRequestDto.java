package com.skhu.oauthgoogleloginpr.dto.user;

public record SignupRequestDto(
        String username,
        String email,
        String password
) {
}
