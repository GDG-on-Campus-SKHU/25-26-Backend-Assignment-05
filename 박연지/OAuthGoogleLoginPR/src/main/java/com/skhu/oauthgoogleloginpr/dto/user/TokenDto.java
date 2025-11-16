package com.skhu.oauthgoogleloginpr.dto.user;

import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
