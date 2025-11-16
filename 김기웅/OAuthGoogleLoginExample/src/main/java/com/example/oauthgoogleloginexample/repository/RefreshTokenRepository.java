package com.example.oauthgoogleloginexample.repository;

import com.example.oauthgoogleloginexample.domain.RefreshToken;
import com.example.oauthgoogleloginexample.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(UserAccount user);
}
