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

    @Column(name = "USER_EMAIL", nullable = false)
    private String email;

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_PICTURE", nullable = false)
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_PROVIDER")
    private Provider provider;

    @Column(name = "USER_ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "USER_REFRESH_TOKEN")
    private String refreshToken;

    @Builder
    public User(String name, String email, String password, String pictureUrl, Role role, Provider provider, String accessToken, String refreshToken) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.pictureUrl = pictureUrl;
        this.role = role;
        this.provider = provider;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void saveAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoList> todos = new ArrayList<>();


}