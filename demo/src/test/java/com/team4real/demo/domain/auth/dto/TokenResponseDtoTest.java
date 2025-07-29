package com.team4real.demo.domain.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenResponseDto 테스트")
class TokenResponseDtoTest {

    @Test
    @DisplayName("TokenResponseDto 생성 - 성공")
    void createTokenResponseDto_Success() {
        // given
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_456";

        // when
        TokenResponseDto responseDto = new TokenResponseDto(accessToken, refreshToken);

        // then
        assertThat(responseDto.accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.refreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("TokenResponseDto 생성 - null 토큰")
    void createTokenResponseDto_WithNullTokens_Success() {
        // given
        String accessToken = null;
        String refreshToken = null;

        // when
        TokenResponseDto responseDto = new TokenResponseDto(accessToken, refreshToken);

        // then
        assertThat(responseDto.accessToken()).isNull();
        assertThat(responseDto.refreshToken()).isNull();
    }

    @Test
    @DisplayName("TokenResponseDto 생성 - 빈 문자열 토큰")
    void createTokenResponseDto_WithEmptyTokens_Success() {
        // given
        String accessToken = "";
        String refreshToken = "";

        // when
        TokenResponseDto responseDto = new TokenResponseDto(accessToken, refreshToken);

        // then
        assertThat(responseDto.accessToken()).isEqualTo("");
        assertThat(responseDto.refreshToken()).isEqualTo("");
    }

    @Test
    @DisplayName("TokenResponseDto record 구조 확인")
    void tokenResponseDtoRecordStructure() {
        // given
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_456";

        // when
        TokenResponseDto responseDto = new TokenResponseDto(accessToken, refreshToken);

        // then
        assertThat(responseDto).isInstanceOf(TokenResponseDto.class);
        assertThat(responseDto.accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.refreshToken()).isEqualTo(refreshToken);
    }
} 