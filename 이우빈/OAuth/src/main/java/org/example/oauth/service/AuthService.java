package org.example.oauth.service;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.user.Provider;
import org.example.oauth.domain.user.Role;
import org.example.oauth.domain.user.User;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.dto.user.request.LoginRequest;
import org.example.oauth.dto.user.request.SignUpRequest;
import org.example.oauth.exception.BadRequestException;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.jwt.TokenProvider;
import org.example.oauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail())
                .ifPresent(user -> {
                    throw new BadRequestException(ErrorMessage.ALREADY_EXIST_EMAIL);
                });

        userRepository.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .name(signUpRequest.getName())
                .role(Role.ROLE_USER)
                .provider(Provider.LOCAL)
                .providerId(null)
                .build());
    }

    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        return tokenService.saveAndReturnToken(user.getId(), user.getRole().name());
    }
}
