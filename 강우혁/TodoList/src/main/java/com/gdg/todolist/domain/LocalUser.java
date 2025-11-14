package com.gdg.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "local_user")
public class LocalUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LOCAL_USER_NAME", nullable = false)
    private String name;

    @Column(name = "LOCAL_USER_EMAIL", nullable = false)
    private String email;

    @Column(name = "LOCAL_USER_PASSWORD")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOCAL_USER_ROLE")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOCAL_USER_PROVIDER")
    private Provider provider;

    @Column(name = "LOCAL_USER_ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "LOCAL_USER_REFRESH_TOKEN")
    private String refreshToken;

    @Builder
    public LocalUser(String name, String email, String password, Role role, Provider provider, String accessToken, String refreshToken) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    @OneToMany(mappedBy = "localUser", cascade = CascadeType.ALL)
    private List<TodoList> todos = new ArrayList<>();
}
