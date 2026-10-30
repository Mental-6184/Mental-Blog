package org.example.mentalblogh.common.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private final StringRedisTemplate redis;

    public LoginAttemptService(StringRedisTemplate redis) { this.redis = redis; }

    public boolean blocked(String username, String ip) {
        try { return count("user", username) >= MAX_ATTEMPTS || count("ip", ip) >= MAX_ATTEMPTS; }
        catch (RuntimeException ignored) { return false; }
    }

    public void failure(String username, String ip) { increment("user", username); increment("ip", ip); }

    public void success(String username, String ip) {
        try { redis.delete(key("user", username)); redis.delete(key("ip", ip)); }
        catch (RuntimeException ignored) { }
    }

    private void increment(String type, String value) {
        try {
            String key = key(type, value);
            Long count = redis.opsForValue().increment(key);
            if (count != null && count == 1) redis.expire(key, 15, TimeUnit.MINUTES);
        } catch (RuntimeException ignored) { }
    }

    private long count(String type, String value) {
        String raw = redis.opsForValue().get(key(type, value));
        return raw == null ? 0 : Long.parseLong(raw);
    }

    private String key(String type, String value) { return "auth:login-fail:" + type + ":" + value; }
}
