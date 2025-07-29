package com.team4real.demo.global.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisServiceTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisService = new RedisService(redisTemplate);
    }

    @Test
    @DisplayName("set(key, value) 호출 시 RedisTemplate에 저장")
    void setWithoutTTL() {
        redisService.set("testKey", "testValue");

        verify(valueOperations).set("testKey", "testValue");
    }

    @Test
    @DisplayName("set(key, value, ttl) 호출 시 TTL과 함께 저장")
    void setWithTTL() {
        Duration ttl = Duration.ofSeconds(10);

        redisService.set("testKey", "testValue", ttl);

        verify(valueOperations).set("testKey", "testValue", ttl);
    }

    @Test
    @DisplayName("get(key, clazz) 호출 시 값 반환")
    void get() {
        when(valueOperations.get("testKey")).thenReturn("testValue");

        String result = redisService.get("testKey", String.class);

        assertEquals("testValue", result);
    }

    @Test
    @DisplayName("delete(key) 호출 시 RedisTemplate 삭제 호출")
    void delete() {
        redisService.delete("testKey");

        verify(redisTemplate).delete("testKey");
    }

    @Test
    @DisplayName("hasKey(key) → true 반환")
    void hasKeyTrue() {
        when(redisTemplate.hasKey("testKey")).thenReturn(true);

        assertTrue(redisService.hasKey("testKey"));
    }

    @Test
    @DisplayName("hasKey(key) → false 반환")
    void hasKeyFalse() {
        when(redisTemplate.hasKey("testKey")).thenReturn(false);

        assertFalse(redisService.hasKey("testKey"));
    }

    @Test
    @DisplayName("hasKey(key) → null 반환 시 false 처리")
    void hasKeyNullSafe() {
        when(redisTemplate.hasKey("testKey")).thenReturn(null);

        assertFalse(redisService.hasKey("testKey"));
    }
}
