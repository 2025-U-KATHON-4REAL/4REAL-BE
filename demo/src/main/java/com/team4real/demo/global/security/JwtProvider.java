package com.team4real.demo.global.security;

import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key accessKey;
    private final Key refreshKey;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Duration ACCESS_TOKEN_EXPIRE_TIME = Duration.ofHours(6);
    private static final Duration REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7);
    private final RedisService redisService;

    public JwtProvider(@Value("${jwt.secret.access}") String accessSecret,
                       @Value("${jwt.secret.refresh}") String refreshSecret,
                       CustomUserDetailsService customUserDetailsService,
                       RedisService redisService) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        this.customUserDetailsService = customUserDetailsService;
        this.redisService = redisService;
    }

    public String generateAccessToken(String username) {
        return generateToken(username, accessKey, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshKey, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String generateToken(String username, Key key, Duration expiredTime) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", Role.CREATOR.toAuthority());

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiredTime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(refreshToken);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken, accessKey);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Claims getAccessTokenClaims(String accessToken) {
        return parseClaims(accessToken, accessKey);
    }

    private Claims parseClaims(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Claims getRefreshTokenClaims(String refreshToken) {
        return parseClaims(refreshToken, refreshKey);
    }


    // Redis에 refreshToken 저장 (7일 TTL 설정)
    public void updateRefreshToken(String username, String refreshToken) {
        String redisKey = "auth:refresh_token:" + username;
        redisService.set(redisKey, refreshToken, Duration.ofDays(7));
    }

    // Redis에서 refreshToken 조회
    public String getRefreshToken(String username) {
        String redisKey = "auth:refresh_token:" + username;
        return redisService.get(redisKey, String.class);
    }

    // Redis에서 refreshToken 삭제
    public void deleteRefreshToken(String username) {
        String redisKey = "auth:refresh_token:" + username;
        redisService.delete(redisKey);
    }
}