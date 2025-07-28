package com.team4real.demo.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(
        @NotBlank String email,
        @NotBlank String password
) {
}