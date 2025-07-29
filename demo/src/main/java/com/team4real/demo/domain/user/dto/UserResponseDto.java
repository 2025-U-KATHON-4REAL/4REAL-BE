package com.team4real.demo.domain.user.dto;

import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long userId,
        String email,
        String nickname,
        String phoneNumber,
        UserType userType,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
} 