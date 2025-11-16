package com.gdg.todolist.service;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.User;
import com.gdg.todolist.dto.LocalLoginRequestDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

   @Transactional
    public TokenDto login(LocalLoginRequestDto localLoginRequestDto) {
        User user = userRepository.findByEmail(localLoginRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 이메일입니다."));

        if (user.getProvider() != Provider.LOCAL)
            throw new RuntimeException("소셜 로그인 유저입니다. 구글 로그인으로 이용해주세요.");

        if (!passwordEncoder.matches(localLoginRequestDto.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        return tokenProvider.createToken(user);
    }
}

