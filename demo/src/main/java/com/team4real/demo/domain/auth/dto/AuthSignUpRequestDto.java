package com.team4real.demo.domain.auth.dto;

import com.team4real.demo.domain.user.entity.UserType;
import jakarta.validation.constraints.NotBlank;

public record AuthSignUpRequestDto (
        @NotBlank UserType userType, // "creator" or "brand" or "admin"
        @NotBlank String nickname,
        @NotBlank String phoneNumber,
        @NotBlank String email,
        @NotBlank String password
) {
}