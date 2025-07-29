package com.team4real.demo.domain.auth.service;

import com.team4real.demo.domain.auth.dto.AuthLoginRequestDto;
import com.team4real.demo.domain.auth.dto.AuthSignUpRequestDto;
import com.team4real.demo.domain.auth.dto.TokenResponseDto;
import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
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
    private final AuthUserService authUserService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void validateEmailAvailability(String email) {
        if (authUserService.existsByEmail(email)) {
            throw new CustomException(ErrorCode.RESOURCE_ALREADY_EXISTS);
        }
    }

    // 회원 가입
    public TokenResponseDto signUp(AuthSignUpRequestDto requestDto) {
        validateEmailAvailability(requestDto.email());
        String normalizedPhoneNumber = requestDto.phoneNumber().replaceAll("-", "");
        AuthUser newAuthUser = authUserService.createAuthUser(
                requestDto.email(),
                encodePassword(requestDto.password()),
                requestDto.role(),
                normalizedPhoneNumber
        );
        if (newAuthUser.isCreator()) {
            authUserService.createCreator(newAuthUser, requestDto.name());
        } else if (newAuthUser.isBrand()) {
            authUserService.createBrand(newAuthUser, requestDto.name());
        }
        return generateTokenResponse(newAuthUser);
    }

    // 로그인
    public TokenResponseDto login(AuthLoginRequestDto requestDto) {
        AuthUser authUser = authUserService.getAuthUserByEmail(requestDto.email());
        if (!passwordEncoder.matches(requestDto.password(), authUser.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        return generateTokenResponse(authUser);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        Claims claims = jwtProvider.getRefreshTokenClaims(refreshToken);
        String userEmail = claims.getSubject();
        AuthUser authUser = authUserService.getAuthUserByEmail(userEmail);

        String storedRefreshToken = jwtProvider.getRefreshToken(userEmail);

        if (authUser == null || storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        String newAccessToken = jwtProvider.generateAccessToken(authUser.getEmail());
        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    private TokenResponseDto generateTokenResponse(AuthUser authUser) {
        String accessToken = jwtProvider.generateAccessToken(authUser.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(authUser.getEmail());

        jwtProvider.updateRefreshToken(authUser.getEmail(), refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}