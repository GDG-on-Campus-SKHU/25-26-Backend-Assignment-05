package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.user.SignupRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.UserInfoResponseDto;
import com.skhu.oauthgoogleloginpr.global.code.ErrorStatus;
import com.skhu.oauthgoogleloginpr.global.exception.GeneralException;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public UserInfoResponseDto signup(SignupRequestDto requestDto) {
        validateDuplicateEmail(requestDto.email());

        User user = User.builder()
                .username(requestDto.username())
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .role(Role.USER)
                .build();

        return UserInfoResponseDto.from(userRepository.save(user));
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
        }
    }
}

