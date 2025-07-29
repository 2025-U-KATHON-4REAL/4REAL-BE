package com.team4real.demo.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User 엔티티 테스트")
class UserTest {

    @Test
    @DisplayName("User 생성 성공")
    void createUser_Success() {
        // given
        String email = "test@example.com";
        String encryptedPassword = "encryptedPassword123";
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        UserType userType = UserType.CREATOR;

        // when
        User user = User.builder()
                .email(email)
                .encryptedPassword(encryptedPassword)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .userType(userType)
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getEncryptedPassword()).isEqualTo(encryptedPassword);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getUserType()).isEqualTo(userType);
    }

    @Test
    @DisplayName("User 생성 시 필수 필드 검증")
    void createUser_WithRequiredFields_Success() {
        // given
        String email = "test@example.com";
        String encryptedPassword = "encryptedPassword123";
        String nickname = "테스트유저";
        UserType userType = UserType.BRAND;

        // when
        User user = User.builder()
                .email(email)
                .encryptedPassword(encryptedPassword)
                .nickname(nickname)
                .userType(userType)
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getEncryptedPassword()).isEqualTo(encryptedPassword);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getUserType()).isEqualTo(userType);
        assertThat(user.getPhoneNumber()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATOR", "BRAND", "ADMIN"})
    @DisplayName("다양한 UserType으로 User 생성")
    void createUser_WithDifferentUserTypes_Success(String userTypeName) {
        // given
        UserType userType = UserType.valueOf(userTypeName);
        String email = "test@example.com";
        String encryptedPassword = "encryptedPassword123";
        String nickname = "테스트유저";

        // when
        User user = User.builder()
                .email(email)
                .encryptedPassword(encryptedPassword)
                .nickname(nickname)
                .userType(userType)
                .build();

        // then
        assertThat(user.getUserType()).isEqualTo(userType);
    }

    @Test
    @DisplayName("User 생성 시 BaseTimeEntity 상속 확인")
    void createUser_ExtendsBaseTimeEntity() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .userType(UserType.CREATOR)
                .build();

        // when & then
        assertThat(user).isInstanceOf(User.class);
        // BaseTimeEntity의 메서드들이 접근 가능한지 확인
        assertThat(user.getCreatedAt()).isNull(); // 저장 전에는 null
        assertThat(user.getModifiedAt()).isNull(); // 저장 전에는 null
    }
} 