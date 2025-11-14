package com.gdg.todolist.service;

import com.gdg.todolist.domain.LocalUser;
import com.gdg.todolist.dto.LoginDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.LocalUserRepository;
import com.gdg.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LocalUserRepository localuserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenDto login(LoginDto login) {
        LocalUser localUser = localuserRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
        if (!passwordEncoder.matches(login.getPassword(), localUser.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = tokenProvider.createAccessToken(localUser);
        String refreshToken = tokenProvider.createRefreshToken(localUser);
        return TokenDto.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }
}

