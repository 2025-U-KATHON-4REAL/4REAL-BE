package com.team4real.demo.domain.auth.dto;

public record TokenResponseDto (
        String accessToken,
        String refreshToken
) {
}