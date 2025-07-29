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

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

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
        String storedRefreshToken = jwtProvider.getRefreshToken(userEmail);
        
        if (user == null || storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail());
        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    private TokenResponseDto generateTokenResponse(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        jwtProvider.updateRefreshToken(user.getEmail(), refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}