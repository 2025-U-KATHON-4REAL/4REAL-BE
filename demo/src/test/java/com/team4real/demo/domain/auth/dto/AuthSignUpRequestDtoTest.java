package com.team4real.demo.domain.auth.dto;

import com.team4real.demo.domain.user.entity.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthSignUpRequestDto 테스트")
class AuthSignUpRequestDtoTest {

    @Test
    @DisplayName("AuthSignUpRequestDto 생성 - 성공")
    void createAuthSignUpRequestDto_Success() {
        // given
        UserType userType = UserType.CREATOR;
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        String email = "test@example.com";
        String password = "password123";

        // when
        AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(
                userType, nickname, phoneNumber, email, password
        );

        // then
        assertThat(requestDto.userType()).isEqualTo(userType);
        assertThat(requestDto.nickname()).isEqualTo(nickname);
        assertThat(requestDto.phoneNumber()).isEqualTo(phoneNumber);
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATOR", "BRAND", "ADMIN"})
    @DisplayName("다양한 UserType으로 AuthSignUpRequestDto 생성")
    void createAuthSignUpRequestDto_WithDifferentUserTypes_Success(String userTypeName) {
        // given
        UserType userType = UserType.valueOf(userTypeName);
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        String email = "test@example.com";
        String password = "password123";

        // when
        AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(
                userType, nickname, phoneNumber, email, password
        );

        // then
        assertThat(requestDto.userType()).isEqualTo(userType);
        assertThat(requestDto.nickname()).isEqualTo(nickname);
        assertThat(requestDto.phoneNumber()).isEqualTo(phoneNumber);
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("AuthSignUpRequestDto record 구조 확인")
    void authSignUpRequestDtoRecordStructure() {
        // given
        UserType userType = UserType.CREATOR;
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        String email = "test@example.com";
        String password = "password123";

        // when
        AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(
                userType, nickname, phoneNumber, email, password
        );

        // then
        assertThat(requestDto).isInstanceOf(AuthSignUpRequestDto.class);
        assertThat(requestDto.userType()).isEqualTo(userType);
        assertThat(requestDto.nickname()).isEqualTo(nickname);
        assertThat(requestDto.phoneNumber()).isEqualTo(phoneNumber);
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }
} 