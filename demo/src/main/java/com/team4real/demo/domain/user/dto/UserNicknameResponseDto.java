package com.team4real.demo.domain.user.dto;

public record UserNicknameResponseDto(String nickname) {
    public static UserNicknameResponseDto from(String nickname) {
        return new UserNicknameResponseDto(nickname);
    }
} 