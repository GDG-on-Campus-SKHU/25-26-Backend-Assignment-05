package com.example.oauthgoogleloginexample.repository;


import com.example.oauthgoogleloginexample.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);
    boolean existsByKeyCode(String keyCode);
}
