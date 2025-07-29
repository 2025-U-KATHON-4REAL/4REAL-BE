package com.team4real.demo.domain.auth.dto;

public record AuthProfileResponseDto(
        String name,
        String image,
        String email
) {
    public static AuthProfileResponseDto from(String name, String image, String email) {
        return new AuthProfileResponseDto(name, image, email);
    }
}