package com.gdg.todolist.dto;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Provider provider;

    @Builder
    public UserInfoResponseDto(Long id, String name, String email, Role role, Provider provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.provider = provider;
    }

    public static UserInfoResponseDto from(User user){
        return UserInfoResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .provider(user.getProvider())
                .build();
    }
}
