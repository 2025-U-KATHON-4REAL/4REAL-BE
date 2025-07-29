package com.team4real.demo.domain.auth.dto;

import com.team4real.demo.domain.auth.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthSignUpRequestDto (
        @NotNull Role role, // "creator" or "brand" or "admin"
        @NotBlank String nickname,
        @NotBlank @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 숫자 10~11자리여야 합니다.") String phoneNumber,
        @NotBlank String email,
        @NotBlank String password
) {
}