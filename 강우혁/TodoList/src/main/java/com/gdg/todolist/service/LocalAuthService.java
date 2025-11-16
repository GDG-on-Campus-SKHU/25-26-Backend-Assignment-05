package com.gdg.todolist.service;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.domain.User;
import com.gdg.todolist.dto.LocalSignupRequestDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.UserInfoResponseDto;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto adminSingUp(LocalSignupRequestDto localSignupRequestDto) {
        User user = userRepository.save(User.builder()
                .name(localSignupRequestDto.getName())
                .email(localSignupRequestDto.getEmail())
                .password(passwordEncoder.encode(localSignupRequestDto.getPassword()))
                .provider(Provider.LOCAL)
                .role(Role.ROLE_ADMIN)
                .providerId(null)
                .pictureUrl(null)
                .build()
        );

        return tokenProvider.createToken(user);
    }

    public TokenDto userSignUp(LocalSignupRequestDto localSignupRequestDto) {
        User user = userRepository.save(User.builder()
                .name(localSignupRequestDto.getName())
                .email(localSignupRequestDto.getEmail())
                .password(passwordEncoder.encode(localSignupRequestDto.getPassword()))
                .provider(Provider.LOCAL)
                .role(Role.ROLE_USER)
                .providerId(null)
                .pictureUrl(null)
                .build()
        );

        return tokenProvider.createToken(user);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getMyInfo(Principal principal) {
        User user = findUser(Long.parseLong(principal.getName()));
        return UserInfoResponseDto.from(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long id) {
        User user = findUser(id);
        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public UserInfoResponseDto update(Principal principal, LocalSignupRequestDto localSignupRequestDto) {
        User user = findUser(Long.parseLong(principal.getName()));

        user.updateInfo(
                localSignupRequestDto.getName() == null ? user.getName() : localSignupRequestDto.getName(),
                localSignupRequestDto.getEmail() == null ? user.getEmail() : localSignupRequestDto.getEmail(),
                localSignupRequestDto.getPassword() == null ? user.getPassword() : passwordEncoder.encode(localSignupRequestDto.getPassword())
        );

        return UserInfoResponseDto.from(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserInfoResponseDto adminUpdate(Long id, LocalSignupRequestDto localSignupRequestDto) {
        User user = findUser(id);

        user.updateInfo(
                localSignupRequestDto.getName() == null ? user.getName() : localSignupRequestDto.getName(),
                localSignupRequestDto.getEmail() == null ? user.getEmail() : localSignupRequestDto.getEmail(),
                localSignupRequestDto.getPassword() == null ? user.getPassword() : passwordEncoder.encode(localSignupRequestDto.getPassword())
        );

        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Principal principal) {
        userRepository.deleteById(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
    }
}
