package com.gdg.todolist.service;

import com.gdg.todolist.domain.LocalUser;
import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.dto.LocalUserInfoDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.LocalSignupRequestDto;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.LocalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final LocalUserRepository localUserRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto adminSignUp(LocalSignupRequestDto localSignupRequestDto) {
        LocalUser localUser = localUserRepository.save(LocalUser.builder()
                .name(localSignupRequestDto.getName())
                .email(localSignupRequestDto.getEmail())
                .password(passwordEncoder.encode(localSignupRequestDto.getPassword()))
                .role(Role.ROLE_ADMIN)
                .provider(Provider.LOCAL)
                .build()
        );

        String accessToken = tokenProvider.createAccessToken(localUser);
        String refreshToken = tokenProvider.createRefreshToken(localUser);

        localUser.saveAccessToken(accessToken);
        localUser.saveRefreshToken(refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenDto userSignUp(LocalSignupRequestDto localSignupRequestDto) {
        LocalUser localUser = localUserRepository.save(LocalUser.builder()
                .name(localSignupRequestDto.getName())
                .email(localSignupRequestDto.getEmail())
                .password(passwordEncoder.encode(localSignupRequestDto.getPassword()))
                .role(Role.ROLE_USER)
                .provider(Provider.LOCAL)
                .build()
        );

        String accessToken = tokenProvider.createAccessToken(localUser);
        String refreshToken = tokenProvider.createRefreshToken(localUser);

        localUser.saveAccessToken(accessToken);
        localUser.saveRefreshToken(refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public LocalUserInfoDto getMyInfo(Principal principal){
        LocalUser user = entityUserId(Long.parseLong(principal.getName()));

        return LocalUserInfoDto.from(user);
    }

    @Transactional(readOnly = true)
    public LocalUserInfoDto getUserInfo(Long id){
        LocalUser user = entityUserId(id);

        return LocalUserInfoDto.from(user);
    }

    @Transactional
    public LocalUserInfoDto update(Long id, LocalSignupRequestDto localSignupRequestDto) {
        LocalUser localUser = entityUserId(id);

        localUser.updateInfo(
                localSignupRequestDto.getName(),
                localSignupRequestDto.getEmail(),
                localSignupRequestDto.getPassword()
        );

        localUserRepository.save(localUser);
        return LocalUserInfoDto.from(localUser);
    }

    @Transactional
    public void deleteUser(Principal principal){
        localUserRepository.deleteById(Long.parseLong(principal.getName()));
    }

    private LocalUser entityUserId(Long id) {
        return localUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
    }
}
