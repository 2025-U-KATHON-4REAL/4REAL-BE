package com.team4real.demo.domain.auth.dto;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;

public record AuthMeResponseDto(
        Long authUserId,
        String email,
        Role role,
        Long roleId
) {
    public static AuthMeResponseDto from(AuthUser authUser, Long roleId) {
        return new AuthMeResponseDto(authUser.getAuthUserId(), authUser.getEmail(), authUser.getRole(), roleId);
    }
}
