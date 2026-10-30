package org.example.mentalblogh.common.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

@Service
public class TokenSecurityService {
    private final StringRedisTemplate redis;

    public TokenSecurityService(StringRedisTemplate redis) { this.redis = redis; }

    public void revoke(String token, long ttlMillis) {
        try { redis.opsForValue().set(key(token), "1", ttlMillis, TimeUnit.MILLISECONDS); }
        catch (RuntimeException ignored) { }
    }

    public boolean isRevoked(String token) {
        try { return Boolean.TRUE.equals(redis.hasKey(key(token))); }
        catch (RuntimeException ignored) { return false; }
    }

    private String key(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder("auth:revoked:");
            for (byte b : digest) out.append(String.format("%02x", b));
            return out.toString();
        } catch (Exception e) { return "auth:revoked:" + token.hashCode(); }
    }
}
