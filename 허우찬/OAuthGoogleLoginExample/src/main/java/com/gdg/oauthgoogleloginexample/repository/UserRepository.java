package com.gdg.oauthgoogleloginexample.repository;

import com.gdg.oauthgoogleloginexample.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
