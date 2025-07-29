package com.team4real.demo.domain.auth.controller;

import com.team4real.demo.domain.auth.dto.AuthLoginRequestDto;
import com.team4real.demo.domain.auth.dto.AuthSignUpRequestDto;
import com.team4real.demo.domain.auth.dto.TokenResponseDto;
import com.team4real.demo.domain.auth.service.AuthService;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("이메일 중복 확인 - 성공")
    void checkEmailAvailability_Success() {
        // given
        String email = "test@example.com";

        // when
        ResponseEntity<String> response = authController.checkEmailAvailability(email);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).validateEmailAvailability(email);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 이미 존재하는 경우")
    void checkEmailAvailability_WhenEmailExists_ThrowsException() {
        // given
        String email = "test@example.com";
        doThrow(new CustomException(ErrorCode.RESOURCE_ALREADY_EXISTS))
                .when(authService).validateEmailAvailability(email);

        // when & then
        assertThatThrownBy(() -> authController.checkEmailAvailability(email))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_ALREADY_EXISTS);
        verify(authService).validateEmailAvailability(email);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signUp_Success() {
        // given
        AuthSignUpRequestDto requestDto = createSignUpRequestDto();
        TokenResponseDto expectedResponse = new TokenResponseDto("access_token", "refresh_token");
        when(authService.signUp(requestDto)).thenReturn(expectedResponse);

        // when
        ResponseEntity<TokenResponseDto> response = authController.signUp(requestDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(authService).signUp(requestDto);
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복으로 실패")
    void signUp_WhenEmailExists_ThrowsException() {
        // given
        AuthSignUpRequestDto requestDto = createSignUpRequestDto();
        when(authService.signUp(requestDto))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_ALREADY_EXISTS));

        // when & then
        assertThatThrownBy(() -> authController.signUp(requestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_ALREADY_EXISTS);
        verify(authService).signUp(requestDto);
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_Success() {
        // given
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto("test@example.com", "password123");
        TokenResponseDto expectedResponse = new TokenResponseDto("access_token", "refresh_token");
        when(authService.login(requestDto)).thenReturn(expectedResponse);

        // when
        ResponseEntity<TokenResponseDto> response = authController.login(requestDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(authService).login(requestDto);
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호로 실패")
    void login_WithInvalidPassword_ThrowsException() {
        // given
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto("test@example.com", "wrongpassword");
        when(authService.login(requestDto))
                .thenThrow(new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // when & then
        assertThatThrownBy(() -> authController.login(requestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
        verify(authService).login(requestDto);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 성공")
    void refreshAccessToken_Success() {
        // given
        String refreshToken = "valid_refresh_token";
        TokenResponseDto expectedResponse = new TokenResponseDto("new_access_token", refreshToken);
        when(authService.refreshAccessToken(refreshToken)).thenReturn(expectedResponse);

        // when
        ResponseEntity<TokenResponseDto> response = authController.refreshAccessToken(refreshToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(authService).refreshAccessToken(refreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 만료된 refreshToken으로 실패")
    void refreshAccessToken_WithExpiredToken_ThrowsException() {
        // given
        String refreshToken = "expired_refresh_token";
        when(authService.refreshAccessToken(refreshToken))
                .thenThrow(new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN));

        // when & then
        assertThatThrownBy(() -> authController.refreshAccessToken(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_REFRESH_TOKEN);
        verify(authService).refreshAccessToken(refreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 잘못된 refreshToken으로 실패")
    void refreshAccessToken_WithInvalidToken_ThrowsException() {
        // given
        String refreshToken = "invalid_refresh_token";
        when(authService.refreshAccessToken(refreshToken))
                .thenThrow(new CustomException(ErrorCode.INVALID_TOKEN));

        // when & then
        assertThatThrownBy(() -> authController.refreshAccessToken(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
        verify(authService).refreshAccessToken(refreshToken);
    }

    private AuthSignUpRequestDto createSignUpRequestDto() {
        return new AuthSignUpRequestDto(
                UserType.CREATOR,
                "테스트유저",
                "010-1234-5678",
                "test@example.com",
                "password123"
        );
    }
} 