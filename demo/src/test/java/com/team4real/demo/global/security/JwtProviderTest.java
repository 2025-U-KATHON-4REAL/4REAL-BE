package com.team4real.demo.global.security;

import com.team4real.demo.domain.user.entity.Role;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtProvider 테스트")
class JwtProviderTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RedisService redisService;

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String accessSecret = "dGVzdF9hY2Nlc3Nfc2VjcmV0X2tleV9mb3JfdGVzdGluZ19wdXJwb3Nlc19vbmx5";
        String refreshSecret = "dGVzdF9yZWZyZXNoX3NlY3JldF9rZXlfZm9yX3Rlc3RpbmdfcHVycG9zZXNfb25seQ==";

        jwtProvider = new JwtProvider(accessSecret, refreshSecret, customUserDetailsService, redisService);
    }

    @Test
    @DisplayName("액세스 토큰 생성 및 검증")
    void generateAndValidateAccessToken() {
        String username = "test@example.com";
        String token = jwtProvider.generateAccessToken(username);

        assertThat(token).isNotNull();
        jwtProvider.validateAccessToken(token);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 검증")
    void generateAndValidateRefreshToken() {
        String username = "test@example.com";
        String token = jwtProvider.generateRefreshToken(username);

        assertThat(token).isNotNull();
        jwtProvider.validateRefreshToken(token);
    }

    @Test
    @DisplayName("액세스 토큰 검증 실패 - 잘못된 토큰")
    void validateAccessToken_Invalid() {
        String invalid = "invalid.token.here";
        assertThatThrownBy(() -> jwtProvider.validateAccessToken(invalid))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("리프레시 토큰 검증 실패 - 잘못된 토큰")
    void validateRefreshToken_Invalid() {
        String invalid = "invalid.token.here";
        assertThatThrownBy(() -> jwtProvider.validateRefreshToken(invalid))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("AccessToken에서 Authentication 생성")
    void getAuthentication() {
        String username = "test@example.com";
        String token = jwtProvider.generateAccessToken(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        var auth = jwtProvider.getAuthentication(token);
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(username);
    }

    @Test
    @DisplayName("RefreshToken에서 Claims 추출")
    void getRefreshTokenClaims() {
        String username = "test@example.com";
        String token = jwtProvider.generateRefreshToken(username);

        Claims claims = jwtProvider.getRefreshTokenClaims(token);
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.ROLE_USER.name());
    }

    @Test
    @DisplayName("AccessToken에서 Claims 추출")
    void getAccessTokenClaims() {
        String username = "test@example.com";
        String token = jwtProvider.generateAccessToken(username);

        Claims claims = jwtProvider.getAccessTokenClaims(token);
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.ROLE_USER.name());
    }

    @Test
    @DisplayName("Redis에 refreshToken 저장")
    void updateRefreshToken() {
        String username = "test@example.com";
        String token = "ref-token";
        jwtProvider.updateRefreshToken(username, token);
        verify(redisService).set("auth:refresh_token:" + username, token, Duration.ofDays(7));
    }

    @Test
    @DisplayName("Redis에서 refreshToken 조회")
    void getRefreshToken() {
        String username = "test@example.com";
        String token = "ref-token";
        when(redisService.get("auth:refresh_token:" + username, String.class)).thenReturn(token);
        assertThat(jwtProvider.getRefreshToken(username)).isEqualTo(token);
    }

    @Test
    @DisplayName("Redis에서 refreshToken 삭제")
    void deleteRefreshToken() {
        String username = "test@example.com";
        jwtProvider.deleteRefreshToken(username);
        verify(redisService).delete("auth:refresh_token:" + username);
    }
}