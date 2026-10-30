package org.example.mentalblogh.controller;

import jakarta.validation.Valid;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.request.LoginRequest;
import org.example.mentalblogh.dto.request.RegisterRequest;
import org.example.mentalblogh.dto.response.LoginVO;
import org.example.mentalblogh.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功，请登录", null);
    }
}
