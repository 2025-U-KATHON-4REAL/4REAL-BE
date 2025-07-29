package com.team4real.demo.global.security;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.repository.AuthUserRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return org.springframework.security.core.userdetails.User.builder()
                .username(authUser.getEmail())
                .password(authUser.getPasswordHash())
                .roles(authUser.getRole().name())
                .build();
    }
}