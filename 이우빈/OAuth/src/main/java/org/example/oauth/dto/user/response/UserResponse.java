package org.example.oauth.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import org.example.oauth.domain.user.User;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String provider;

    public static UserResponse userInfo(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .provider(user.getProvider().name())
                .build();
    }
}

