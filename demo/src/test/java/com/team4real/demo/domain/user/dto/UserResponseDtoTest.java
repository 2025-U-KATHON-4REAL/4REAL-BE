package com.team4real.demo.domain.user.dto;

import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserResponseDto 테스트")
class UserResponseDtoTest {

    @Test
    @DisplayName("User 엔티티로부터 UserResponseDto 생성")
    void from_UserEntity_Success() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .phoneNumber("010-1234-5678")
                .userType(UserType.CREATOR)
                .build();

        // when
        UserResponseDto responseDto = UserResponseDto.from(user);

        // then
        assertThat(responseDto.email()).isEqualTo("test@example.com");
        assertThat(responseDto.nickname()).isEqualTo("테스트유저");
        assertThat(responseDto.phoneNumber()).isEqualTo("010-1234-5678");
        assertThat(responseDto.userType()).isEqualTo(UserType.CREATOR);
    }

    @Test
    @DisplayName("User 엔티티로부터 UserResponseDto 생성 - phoneNumber가 null인 경우")
    void from_UserEntityWithNullPhoneNumber_Success() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .userType(UserType.BRAND)
                .build();

        // when
        UserResponseDto responseDto = UserResponseDto.from(user);

        // then
        assertThat(responseDto.email()).isEqualTo("test@example.com");
        assertThat(responseDto.nickname()).isEqualTo("테스트유저");
        assertThat(responseDto.phoneNumber()).isNull();
        assertThat(responseDto.userType()).isEqualTo(UserType.BRAND);
    }

    @Test
    @DisplayName("다양한 UserType으로 UserResponseDto 생성")
    void from_UserEntityWithDifferentUserTypes_Success() {
        // given
        UserType[] userTypes = {UserType.CREATOR, UserType.BRAND, UserType.ADMIN};

        for (UserType userType : userTypes) {
            User user = User.builder()
                    .email("test@example.com")
                    .encryptedPassword("encryptedPassword123")
                    .nickname("테스트유저")
                    .userType(userType)
                    .build();

            // when
            UserResponseDto responseDto = UserResponseDto.from(user);

            // then
            assertThat(responseDto.userType()).isEqualTo(userType);
        }
    }

    @Test
    @DisplayName("UserResponseDto record 구조 확인")
    void userResponseDtoRecordStructure() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .userType(UserType.CREATOR)
                .build();

        // when
        UserResponseDto responseDto = UserResponseDto.from(user);

        // then
        assertThat(responseDto).isInstanceOf(UserResponseDto.class);
        assertThat(responseDto.userId()).isNull(); // 저장 전에는 null
        assertThat(responseDto.createdAt()).isNull(); // 저장 전에는 null
        assertThat(responseDto.updatedAt()).isNull(); // 저장 전에는 null
    }
} 