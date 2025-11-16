package com.gdg.todolist.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_NAME", nullable = false)
    private String name;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USER_PASSWORD")
    private String password;

    @Column(name = "USER_PICTURE")
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_PROVIDER")
    private Provider provider;

    @Column(name = "USER_PROVIDER_ID")
    private String providerId;

    @Column(name = "USER_ACCESSTOKEN")
    private String accessToken;

    @Column(name = "USER_REFRESHTOKEN")
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoList> todos = new ArrayList<>();

    @Builder
    public User(String name,
                String email,
                String password,
                String pictureUrl,
                Role role,
                Provider provider,
                String providerId) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.pictureUrl = pictureUrl;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void saveToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
