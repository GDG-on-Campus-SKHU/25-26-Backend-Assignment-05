package com.gdg.todolist.dto;

import com.gdg.todolist.domain.LocalUser;
import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalUserInfoDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Provider provider;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LocalUserInfoDto(Long id, String name, String email, Role role, Provider provider, String accessToken, String refreshToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LocalUserInfoDto from(LocalUser user) {
        return LocalUserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .provider(user.getProvider())
                .accessToken(user.getAccessToken())
                .refreshToken(user.getRefreshToken())
                .build();
    }

    public void updateInfo(LocalSignupRequestDto dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
    }
}
