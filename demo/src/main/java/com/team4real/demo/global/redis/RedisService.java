package com.team4real.demo.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 저장 (시간 제한 없음)
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 저장 (TTL 설정)
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    // 조회
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? (T) value : null;
    }

    // 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 존재 확인
    public boolean hasKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }
}
