package com.team4real.demo.domain.auth;

import com.team4real.demo.domain.auth.controller.AuthController;
import com.team4real.demo.domain.auth.dto.AuthLoginRequestDto;
import com.team4real.demo.domain.auth.dto.AuthSignUpRequestDto;
import com.team4real.demo.domain.auth.dto.TokenResponseDto;
import com.team4real.demo.domain.auth.service.AuthService;
import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Auth 도메인 통합 테스트")
class AuthIntegrationTest {

    @Autowired
    private AuthController authController;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Auth 도메인 전체 플로우 테스트 - 성공 케이스")
    void authDomainFlow_Success() {
        // given
        AuthSignUpRequestDto signUpRequest = createSignUpRequestDto();
        AuthLoginRequestDto loginRequest = new AuthLoginRequestDto("test@example.com", "password123");
        TokenResponseDto expectedSignUpResponse = new TokenResponseDto("signup_access_token", "signup_refresh_token");
        TokenResponseDto expectedLoginResponse = new TokenResponseDto("login_access_token", "login_refresh_token");
        TokenResponseDto expectedRefreshResponse = new TokenResponseDto("new_access_token", "existing_refresh_token");

        when(authService.signUp(signUpRequest)).thenReturn(expectedSignUpResponse);
        when(authService.login(loginRequest)).thenReturn(expectedLoginResponse);
        when(authService.refreshAccessToken("existing_refresh_token")).thenReturn(expectedRefreshResponse);

        // when
        ResponseEntity<TokenResponseDto> signUpResponse = authController.signUp(signUpRequest);
        ResponseEntity<TokenResponseDto> loginResponse = authController.login(loginRequest);
        ResponseEntity<TokenResponseDto> refreshResponse = authController.refreshAccessToken("existing_refresh_token");

        // then
        assertThat(signUpResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(signUpResponse.getBody()).isEqualTo(expectedSignUpResponse);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isEqualTo(expectedLoginResponse);

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refreshResponse.getBody()).isEqualTo(expectedRefreshResponse);

        verify(authService).signUp(signUpRequest);
        verify(authService).login(loginRequest);
        verify(authService).refreshAccessToken("existing_refresh_token");
    }

    @Test
    @DisplayName("Auth 도메인 전체 플로우 테스트 - 실패 케이스")
    void authDomainFlow_Failure() {
        // given
        AuthSignUpRequestDto signUpRequest = createSignUpRequestDto();
        AuthLoginRequestDto loginRequest = new AuthLoginRequestDto("test@example.com", "wrongpassword");

        when(authService.signUp(signUpRequest))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_ALREADY_EXISTS));
        when(authService.login(loginRequest))
                .thenThrow(new CustomException(ErrorCode.INVALID_CREDENTIALS));
        when(authService.refreshAccessToken("invalid_token"))
                .thenThrow(new CustomException(ErrorCode.INVALID_TOKEN));

        // when & then
        assertThatThrownBy(() -> authController.signUp(signUpRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_ALREADY_EXISTS);

        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

        assertThatThrownBy(() -> authController.refreshAccessToken("invalid_token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);

        verify(authService).signUp(signUpRequest);
        verify(authService).login(loginRequest);
        verify(authService).refreshAccessToken("invalid_token");
    }

    @Test
    @DisplayName("이메일 중복 확인 테스트")
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
    @DisplayName("AuthSignUpRequestDto 생성 테스트")
    void authSignUpRequestDtoCreation_Success() {
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

    @Test
    @DisplayName("AuthLoginRequestDto 생성 테스트")
    void authLoginRequestDtoCreation_Success() {
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
    @DisplayName("TokenResponseDto 생성 테스트")
    void tokenResponseDtoCreation_Success() {
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
    @DisplayName("다양한 UserType으로 회원가입 테스트")
    void signUpWithDifferentUserTypes_Success() {
        // given
        UserType[] userTypes = {UserType.CREATOR, UserType.BRAND, UserType.ADMIN};

        for (UserType userType : userTypes) {
            AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(
                    userType, "테스트유저", "010-1234-5678", "test@example.com", "password123"
            );
            TokenResponseDto expectedResponse = new TokenResponseDto("access_token", "refresh_token");
            when(authService.signUp(requestDto)).thenReturn(expectedResponse);

            // when
            ResponseEntity<TokenResponseDto> response = authController.signUp(requestDto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedResponse);
            verify(authService).signUp(requestDto);
        }
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