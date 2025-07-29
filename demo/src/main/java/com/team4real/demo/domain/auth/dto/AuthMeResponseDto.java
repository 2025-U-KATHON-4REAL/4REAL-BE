package com.team4real.demo.domain.auth.dto;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;
import lombok.Builder;

public record AuthMeResponseDto(
        Long authUserId,
        String email,
        Role role
) {
    @Builder
    public AuthMeResponseDto {}

    public static AuthMeResponseDto from(AuthUser authUser) {
        return AuthMeResponseDto.builder()
                .authUserId(authUser.getAuthUserId())
                .email(authUser.getEmail())
                .role(authUser.getRole())
                .build();
    }
}
