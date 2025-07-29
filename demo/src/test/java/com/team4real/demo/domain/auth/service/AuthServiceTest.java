package com.team4real.demo.domain.auth.service;

import com.team4real.demo.domain.auth.dto.AuthLoginRequestDto;
import com.team4real.demo.domain.auth.dto.AuthSignUpRequestDto;
import com.team4real.demo.domain.auth.dto.TokenResponseDto;
import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.domain.user.service.UserService;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import com.team4real.demo.global.security.CustomUserDetailsService;
import com.team4real.demo.global.security.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("이메일 중복 확인 - 사용 가능한 경우")
    void validateEmailAvailability_WhenAvailable_Success() {
        // given
        String email = "test@example.com";
        when(userService.existsByEmail(email)).thenReturn(false);

        // when & then
        authService.validateEmailAvailability(email);
        verify(userService).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 이미 존재하는 경우 예외 발생")
    void validateEmailAvailability_WhenExists_ThrowsException() {
        // given
        String email = "test@example.com";
        when(userService.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.validateEmailAvailability(email))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_ALREADY_EXISTS);
        verify(userService).existsByEmail(email);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signUp_Success() {
        // given
        AuthSignUpRequestDto requestDto = createSignUpRequestDto();
        User createdUser = createTestUser();
        String encodedPassword = "encodedPassword123";
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_456";

        when(userService.existsByEmail(requestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.password())).thenReturn(encodedPassword);
        when(userService.createUser(
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                requestDto.phoneNumber(),
                requestDto.userType()
        )).thenReturn(createdUser);
        when(jwtProvider.generateAccessToken(createdUser.getEmail())).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(createdUser.getEmail())).thenReturn(refreshToken);

        // when
        TokenResponseDto response = authService.signUp(requestDto);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        verify(userService).existsByEmail(requestDto.email());
        verify(passwordEncoder).encode(requestDto.password());
        verify(userService).createUser(
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                requestDto.phoneNumber(),
                requestDto.userType()
        );
        verify(jwtProvider).generateAccessToken(createdUser.getEmail());
        verify(jwtProvider).generateRefreshToken(createdUser.getEmail());
        // verify(redisService).set(anyString(), eq(refreshToken), any(Duration.class));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복으로 실패")
    void signUp_WhenEmailExists_ThrowsException() {
        // given
        AuthSignUpRequestDto requestDto = createSignUpRequestDto();
        when(userService.existsByEmail(requestDto.email())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(requestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_ALREADY_EXISTS);
        verify(userService).existsByEmail(requestDto.email());
        verify(userService, never()).createUser(anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_Success() {
        // given
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto("test@example.com", "password123");
        User user = createTestUser();
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_456";

        when(userService.getUserByEmail(requestDto.email())).thenReturn(user);
        when(passwordEncoder.matches(requestDto.password(), user.getEncryptedPassword())).thenReturn(true);
        when(jwtProvider.generateAccessToken(user.getEmail())).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(user.getEmail())).thenReturn(refreshToken);

        // when
        TokenResponseDto response = authService.login(requestDto);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        verify(userService).getUserByEmail(requestDto.email());
        verify(passwordEncoder).matches(requestDto.password(), user.getEncryptedPassword());
        verify(jwtProvider).generateAccessToken(user.getEmail());
        verify(jwtProvider).generateRefreshToken(user.getEmail());
        //verify(redisService).set(anyString(), eq(refreshToken), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호로 실패")
    void login_WithInvalidPassword_ThrowsException() {
        // given
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto("test@example.com", "wrongpassword");
        User user = createTestUser();

        when(userService.getUserByEmail(requestDto.email())).thenReturn(user);
        when(passwordEncoder.matches(requestDto.password(), user.getEncryptedPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(requestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
        verify(userService).getUserByEmail(requestDto.email());
        verify(passwordEncoder).matches(requestDto.password(), user.getEncryptedPassword());
        verify(jwtProvider, never()).generateAccessToken(anyString());
        verify(jwtProvider, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 성공")
    void refreshAccessToken_Success() {
        // given
        String refreshToken = "valid_refresh_token";
        String userEmail = "test@example.com";
        User user = createTestUser();
        String redisKey = "auth:refresh_token:" + userEmail;
        String storedRefreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token_123";

//        doNothing().when(jwtProvider).validateRefreshToken(refreshToken);
        // when(jwtProvider.getRefreshTokenClaims(refreshToken)).thenReturn(createMockClaims(userEmail));
//        when(userService.getUserByEmail(userEmail)).thenReturn(user);
//        when(redisService.get(redisKey, String.class)).thenReturn(storedRefreshToken);
//        when(jwtProvider.generateAccessToken(user.getEmail())).thenReturn(newAccessToken);
//
//        // when
//        TokenResponseDto response = authService.refreshAccessToken(refreshToken);
//
//        // then
//        assertThat(response.accessToken()).isEqualTo(newAccessToken);
//        assertThat(response.refreshToken()).isEqualTo(refreshToken);
//        verify(jwtProvider).validateRefreshToken(refreshToken);
//        verify(jwtProvider).getRefreshTokenClaims(refreshToken);
//        verify(userService).getUserByEmail(userEmail);
//        verify(redisService).get(redisKey, String.class);
//        verify(jwtProvider).generateAccessToken(user.getEmail());
    }


    @Test
    @DisplayName("액세스 토큰 재발급 - 저장된 refreshToken이 다른 경우 실패")
    void refreshAccessToken_WhenStoredTokenDifferent_ThrowsException() {
        // given
        String refreshToken = "valid_refresh_token";
        String userEmail = "test@example.com";
        User user = createTestUser();
        String storedRefreshToken = "different_refresh_token";

//        // when(jwtProvider.getRefreshTokenClaims(refreshToken)).thenReturn(createMockClaims(userEmail));
//        when(userService.getUserByEmail(userEmail)).thenReturn(user);
//        when(redisService.get("auth:refresh_token:" + userEmail, String.class)).thenReturn(storedRefreshToken);
//
//        // when & then
//        assertThatThrownBy(() -> authService.refreshAccessToken(refreshToken))
//                .isInstanceOf(CustomException.class)
//                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_REFRESH_TOKEN);
//        verify(jwtProvider).validateRefreshToken(refreshToken);
//        verify(jwtProvider).getRefreshTokenClaims(refreshToken);
//        verify(userService).getUserByEmail(userEmail);
//        verify(redisService).get("auth:refresh_token:" + userEmail, String.class);
//        verify(jwtProvider, never()).generateAccessToken(anyString());
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

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .encryptedPassword("encodedPassword123")
                .nickname("테스트유저")
                .phoneNumber("010-1234-5678")
                .userType(UserType.CREATOR)
                .build();
    }

    private Claims createMockClaims(String userEmail) {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(userEmail);
        return claims;
    }
} 