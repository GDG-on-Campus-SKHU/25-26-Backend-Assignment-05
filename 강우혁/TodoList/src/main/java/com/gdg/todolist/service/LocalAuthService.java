package com.gdg.todolist.service;

import com.gdg.todolist.domain.LocalUser;
import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.dto.LocalUserInfoDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.LocalUserSignUpDto;
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
    public TokenDto adminSignUp(LocalUserSignUpDto localUserSignUpDto) {
        LocalUser localUser = localUserRepository.save(LocalUser.builder()
                .name(localUserSignUpDto.getName())
                .email(localUserSignUpDto.getEmail())
                .password(passwordEncoder.encode(localUserSignUpDto.getPassword()))
                .role(Role.ROLE_ADMIN)
                .provider(Provider.LOCAL)
                .build()
        );

        return tokenProvider.localToken(localUser);
    }

    @Transactional
    public TokenDto userSignUp(LocalUserSignUpDto localUserSignUpDto) {
        LocalUser localUser = localUserRepository.save(LocalUser.builder()
                .name(localUserSignUpDto.getName())
                .email(localUserSignUpDto.getEmail())
                .password(passwordEncoder.encode(localUserSignUpDto.getPassword()))
                .role(Role.ROLE_USER)
                .provider(Provider.LOCAL)
                .build()
        );

        return tokenProvider.localToken(localUser);
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
    public void deleteUser(Principal principal){
        localUserRepository.deleteById(Long.parseLong(principal.getName()));
    }

    private LocalUser entityUserId(Long id) {
        return localUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
    }
}
