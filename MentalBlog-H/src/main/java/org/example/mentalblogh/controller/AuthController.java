package org.example.mentalblogh.controller;

import jakarta.validation.Valid;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.request.LoginRequest;
import org.example.mentalblogh.dto.request.RegisterRequest;
import org.example.mentalblogh.dto.response.LoginVO;
import org.example.mentalblogh.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.example.mentalblogh.common.exception.BusinessException;
import org.example.mentalblogh.common.result.ErrorCode;
import org.example.mentalblogh.common.utils.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.mentalblogh.service.MailCodeService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LoginAttemptService attempts;
    private final MailCodeService mailCodes;

    public AuthController(UserService userService, LoginAttemptService attempts, MailCodeService mailCodes) {
        this.userService = userService;
        this.attempts = attempts;
        this.mailCodes = mailCodes;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (attempts.blocked(request.getUsername(), ip)) throw new BusinessException(429, "登录失败次数过多，请15分钟后重试");
        try {
            LoginVO result = userService.login(request);
            attempts.success(request.getUsername(), ip);
            return Result.success(result);
        } catch (RuntimeException e) {
            attempts.failure(request.getUsername(), ip);
            throw e;
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功，请登录", null);
    }

    @PostMapping("/send-email-code")
    public Result<Void> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) return Result.error(400, "邮箱不能为空");
        mailCodes.send("reset", email);
        return Result.success("验证码已发送", null);
    }

    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email != null && userService.getByEmail(email) != null) mailCodes.send("reset", email);
        return Result.success("如果邮箱存在，验证码已发送", null);
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email"), code = body.get("code"), password = body.get("newPassword");
        if (email == null || code == null || password == null || password.length() < 6) return Result.error(400, "参数错误");
        if (!mailCodes.verify("reset", email, code)) return Result.error(400, "验证码无效或已过期");
        userService.resetPassword(email, password);
        return Result.success("密码重置成功", null);
    }
}
