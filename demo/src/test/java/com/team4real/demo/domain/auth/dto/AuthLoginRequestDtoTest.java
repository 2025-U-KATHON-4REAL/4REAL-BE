package com.team4real.demo.domain.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthLoginRequestDto 테스트")
class AuthLoginRequestDtoTest {

    @Test
    @DisplayName("AuthLoginRequestDto 생성 - 성공")
    void createAuthLoginRequestDto_Success() {
        // given
        String email = "test@example.com";
        String password = "password123";

        // when
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        // then
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("AuthLoginRequestDto 생성 - 빈 문자열")
    void createAuthLoginRequestDto_WithEmptyStrings_Success() {
        // given
        String email = "";
        String password = "";

        // when
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        // then
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("AuthLoginRequestDto record 구조 확인")
    void authLoginRequestDtoRecordStructure() {
        // given
        String email = "test@example.com";
        String password = "password123";

        // when
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        // then
        assertThat(requestDto).isInstanceOf(AuthLoginRequestDto.class);
        assertThat(requestDto.email()).isEqualTo(email);
        assertThat(requestDto.password()).isEqualTo(password);
    }
} 