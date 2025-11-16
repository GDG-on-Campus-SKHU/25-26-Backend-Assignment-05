package com.skhu.oauthgoogleloginpr.dto.user;

public record LoginRequestDto(
        String email,
        String password
) {
}
