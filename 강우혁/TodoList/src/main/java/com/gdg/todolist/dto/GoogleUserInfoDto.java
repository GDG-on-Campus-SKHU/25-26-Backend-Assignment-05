package com.gdg.todolist.dto;

import com.gdg.todolist.domain.User;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {
    @SerializedName("sub")
    private String id;
    private String email;

    @SerializedName("email_verified")
    private Boolean verifiedEmail;

    private String name;

    @SerializedName("given_name")
    private String givenName;

    @SerializedName("family_name")
    private String familyName;

    @SerializedName("picture")
    private String pictureUrl;

    private String locale;

    @Builder
    public GoogleUserInfoDto(String id, String email, Boolean verifiedEmail, String name, String givenName, String familyName, String pictureUrl, String locale) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.pictureUrl = pictureUrl;
        this.locale = locale;
    }

    public static GoogleUserInfoDto from(User user) {
        return GoogleUserInfoDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
