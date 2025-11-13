package com.gdg.todolist.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @Builder
    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
