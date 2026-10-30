package org.example.mentalblogh.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class MailCodeService {
    private final StringRedisTemplate redis;
    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final SecureRandom random = new SecureRandom();

    public MailCodeService(StringRedisTemplate redis, ObjectProvider<JavaMailSender> sender,
                            @Value("${MAIL_FROM:}") String from) {
        this.redis = redis; this.sender = sender; this.from = from;
    }

    public void send(String purpose, String email) {
        String limitKey = "mail:limit:" + purpose + ":" + email;
        Boolean allowed = redis.opsForValue().setIfAbsent(limitKey, "1", 60, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(allowed)) throw new IllegalArgumentException("验证码发送过于频繁");
        String code = String.valueOf(100000 + random.nextInt(900000));
        redis.opsForValue().set("mail:code:" + purpose + ":" + email, code, 5, TimeUnit.MINUTES);
        JavaMailSender mailSender = sender.getIfAvailable();
        if (mailSender == null || from == null || from.isBlank()) {
            System.out.println("[MailCode] " + purpose + " " + email + " => " + code);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); message.setTo(email); message.setSubject("MentalBlog 验证码");
        message.setText("你的验证码是 " + code + "，5分钟内有效。");
        mailSender.send(message);
    }

    public boolean verify(String purpose, String email, String code) {
        String key = "mail:code:" + purpose + ":" + email;
        String actual = redis.opsForValue().get(key);
        if (actual == null || !actual.equals(code)) return false;
        redis.delete(key);
        return true;
    }
}
