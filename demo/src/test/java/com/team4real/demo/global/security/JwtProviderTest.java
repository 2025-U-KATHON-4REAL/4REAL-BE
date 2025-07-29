package com.team4real.demo.global.security;

import com.team4real.demo.domain.user.entity.Role;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        // 테스트용 JWT 시크릿 키 설정
        String accessSecret = "dGVzdF9hY2Nlc3Nfc2VjcmV0X2tleV9mb3JfdGVzdGluZ19wdXJwb3Nlc19vbmx5";
        String refreshSecret = "dGVzdF9yZWZyZXNoX3NlY3JldF9rZXlfZm9yX3Rlc3RpbmdfcHVycG9zZXNfb25seQ==";
        
        jwtProvider = new JwtProvider(accessSecret, refreshSecret, customUserDetailsService, redisService);
    }

    @Test
    @DisplayName("액세스 토큰 생성 - 성공")
    void generateAccessToken_Success() {
        // given
        String username = "test@example.com";

        // when
        String accessToken = jwtProvider.generateAccessToken(username);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
        
        // 토큰 검증
        jwtProvider.validateAccessToken(accessToken);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 - 성공")
    void generateRefreshToken_Success() {
        // given
        String username = "test@example.com";

        // when
        String refreshToken = jwtProvider.generateRefreshToken(username);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        
        // 토큰 검증
        jwtProvider.validateRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 검증 - 성공")
    void validateAccessToken_Success() {
        // given
        String username = "test@example.com";
        String accessToken = jwtProvider.generateAccessToken(username);

        // when & then
        jwtProvider.validateAccessToken(accessToken);
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("액세스 토큰 검증 - 잘못된 토큰으로 실패")
    void validateAccessToken_WithInvalidToken_ThrowsException() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateAccessToken(invalidToken))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("리프레시 토큰 검증 - 성공")
    void validateRefreshToken_Success() {
        // given
        String username = "test@example.com";
        String refreshToken = jwtProvider.generateRefreshToken(username);

        // when & then
        jwtProvider.validateRefreshToken(refreshToken);
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("리프레시 토큰 검증 - 잘못된 토큰으로 실패")
    void validateRefreshToken_WithInvalidToken_ThrowsException() {
        // given
        String invalidToken = "invalid.refresh.token.here";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateRefreshToken(invalidToken))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("토큰에서 Authentication 객체 생성 - 성공")
    void getAuthentication_Success() {
        // given
        String username = "test@example.com";
        String accessToken = jwtProvider.generateAccessToken(username);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // when
        var authentication = jwtProvider.getAuthentication(accessToken);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);
        verify(customUserDetailsService).loadUserByUsername(username);
    }

    @Test
    @DisplayName("리프레시 토큰에서 Claims 추출 - 성공")
    void getRefreshTokenClaims_Success() {
        // given
        String username = "test@example.com";
        String refreshToken = jwtProvider.generateRefreshToken(username);

        // when
        Claims claims = jwtProvider.getRefreshTokenClaims(refreshToken);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.ROLE_USER.name());
    }

    @Test
    @DisplayName("Redis에 refreshToken 저장 - 성공")
    void updateRefreshToken_Success() {
        // given
        String username = "test@example.com";
        String refreshToken = "refresh_token_123";

        // when
        jwtProvider.updateRefreshToken(username, refreshToken);

        // then
        verify(redisService).set("auth:refresh_token:" + username, refreshToken, Duration.ofDays(7));
    }

    @Test
    @DisplayName("Redis에서 refreshToken 조회 - 성공")
    void getRefreshToken_Success() {
        // given
        String username = "test@example.com";
        String expectedToken = "refresh_token_123";
        when(redisService.get("auth:refresh_token:" + username, String.class)).thenReturn(expectedToken);

        // when
        String actualToken = jwtProvider.getRefreshToken(username);

        // then
        assertThat(actualToken).isEqualTo(expectedToken);
        verify(redisService).get("auth:refresh_token:" + username, String.class);
    }

    @Test
    @DisplayName("Redis에서 refreshToken 삭제 - 성공")
    void deleteRefreshToken_Success() {
        // given
        String username = "test@example.com";

        // when
        jwtProvider.deleteRefreshToken(username);

        // then
        verify(redisService).delete("auth:refresh_token:" + username);
    }

    @Test
    @DisplayName("토큰 생성 시 Role 정보 포함 확인")
    void generateToken_ContainsRole() {
        // given
        String username = "test@example.com";

        // when
        String accessToken = jwtProvider.generateAccessToken(username);
        Claims claims = jwtProvider.getRefreshTokenClaims(accessToken);

        // then
        assertThat(claims.get("role")).isEqualTo(Role.ROLE_USER.name());
    }

    @Test
    @DisplayName("다양한 사용자명으로 토큰 생성")
    void generateToken_WithDifferentUsernames() {
        // given
        String[] usernames = {"user1@example.com", "user2@example.com", "admin@example.com"};

        for (String username : usernames) {
            // when
            String accessToken = jwtProvider.generateAccessToken(username);
            String refreshToken = jwtProvider.generateRefreshToken(username);

            // then
            assertThat(accessToken).isNotNull();
            assertThat(refreshToken).isNotNull();
            
            Claims accessClaims = jwtProvider.getRefreshTokenClaims(accessToken);
            Claims refreshClaims = jwtProvider.getRefreshTokenClaims(refreshToken);
            
            assertThat(accessClaims.getSubject()).isEqualTo(username);
            assertThat(refreshClaims.getSubject()).isEqualTo(username);
        }
    }
} 