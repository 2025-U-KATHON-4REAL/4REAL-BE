package com.team4real.demo.domain.auth.service;

import com.team4real.demo.domain.auth.dto.AuthLoginRequestDto;
import com.team4real.demo.domain.auth.dto.AuthSignUpRequestDto;
import com.team4real.demo.domain.auth.dto.TokenResponseDto;
import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.service.UserService;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import com.team4real.demo.global.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public void validateEmailAvailability(String email) {
        if (userService.existsByEmail(email)) {
            throw new CustomException(ErrorCode.RESOURCE_ALREADY_EXISTS);
        }
    }

    public TokenResponseDto signUp(AuthSignUpRequestDto requestDto) {
        validateEmailAvailability(requestDto.email());
        User newUser = userService.createUser(
                requestDto.email(),
                encodePassword(requestDto.password()),
                requestDto.nickname(),
                requestDto.phoneNumber(),
                requestDto.userType()
        );
        return generateTokenResponse(newUser);
    }

    public TokenResponseDto login(AuthLoginRequestDto requestDto) {
        User user = userService.getUserByEmail(requestDto.email());
        if (!passwordEncoder.matches(requestDto.password(), user.getEncryptedPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        return generateTokenResponse(user);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        Claims claims = jwtProvider.getRefreshTokenClaims(refreshToken);
        String userEmail = claims.getSubject();
        User user = userService.getUserByEmail(userEmail);

        // Redis에서 저장된 refreshToken 조회
        String storedRefreshToken = getRefreshToken(userEmail);
        
        if (user == null || storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail());
        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    private TokenResponseDto generateTokenResponse(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        updateRefreshToken(user.getEmail(), refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    // Redis에 refreshToken 저장 (7일 TTL 설정)
    public void updateRefreshToken(String username, String refreshToken) {
        String redisKey = "refresh_token:" + username;
        redisService.set(redisKey, refreshToken, Duration.ofDays(7));
    }

    // Redis에서 refreshToken 조회
    public String getRefreshToken(String username) {
        String redisKey = "refresh_token:" + username;
        return redisService.get(redisKey, String.class);
    }

    // Redis에서 refreshToken 삭제
    public void deleteRefreshToken(String username) {
        String redisKey = "refresh_token:" + username;
        redisService.delete(redisKey);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}