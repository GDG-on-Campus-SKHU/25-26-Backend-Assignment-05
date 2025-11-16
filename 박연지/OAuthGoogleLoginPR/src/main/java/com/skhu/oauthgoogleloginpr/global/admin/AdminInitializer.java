package com.skhu.oauthgoogleloginpr.global.admin;

import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            System.out.println("관리자 계정 생성됨: admin@test.com / admin1234");
        };
    }
}
