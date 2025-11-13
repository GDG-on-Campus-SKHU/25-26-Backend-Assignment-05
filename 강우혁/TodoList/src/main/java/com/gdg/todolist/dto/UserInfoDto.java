package com.gdg.todolist.dto;

import com.gdg.todolist.domain.User;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoDto {

    private Long id;
    private String email;

    @SerializedName("verified_email")
    private Boolean verifiedEmail;

    private String name;

    @SerializedName("given_name")
    private String givenName;

    @SerializedName("family_name")
    private String familyName;

    @SerializedName("picture") // 구글이 내려주는 JSON 키 이름이 picture 이므로 그에 맞춰 매핑해야 한다
    private String pictureUrl;

    private String locale;

    @Builder
    public UserInfoDto(Long id, String email, Boolean verifiedEmail, String name, String givenName, String familyName, String pictureUrl, String locale) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.pictureUrl = pictureUrl;
        this.locale = locale;
    }

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
