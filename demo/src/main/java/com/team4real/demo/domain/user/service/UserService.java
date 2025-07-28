package com.team4real.demo.domain.user.service;

import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.domain.user.repository.UserRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import com.team4real.demo.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneId;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public User createUser(String email, String encryptedPassword, String nickname, String phoneNumber, UserType userType) {
        User user = User.builder()
                .email(email)
                .encryptedPassword(encryptedPassword)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .userType(userType)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getCurrentUserNickname() {
        return getCurrentUser().getNickname();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_AUTHENTICATED));
    }

    public void updateRefreshToken(String username, String refreshToken) {
        // Redis에 refreshToken 저장 (7일 TTL 설정)
        String redisKey = "refresh_token:" + username;
        redisService.set(redisKey, refreshToken, Duration.ofDays(7));
    }

    // Redis에서 refreshToken 조회하는 메서드 추가
    public String getRefreshToken(String username) {
        String redisKey = "refresh_token:" + username;
        return redisService.get(redisKey, String.class);
    }

    // Redis에서 refreshToken 삭제하는 메서드 추가
    public void deleteRefreshToken(String username) {
        String redisKey = "refresh_token:" + username;
        redisService.delete(redisKey);
    }
}
