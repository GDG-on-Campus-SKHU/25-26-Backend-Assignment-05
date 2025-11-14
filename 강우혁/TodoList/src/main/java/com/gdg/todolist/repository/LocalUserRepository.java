package com.gdg.todolist.repository;

import com.gdg.todolist.domain.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {
    Optional<LocalUser> findByEmail(String email);
}
