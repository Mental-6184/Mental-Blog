package org.example.mentalblogh.controller;

import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.response.UserInfoVO;
import org.example.mentalblogh.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.mentalblogh.common.utils.JwtUtil;
import org.example.mentalblogh.common.utils.TokenSecurityService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenSecurityService tokenSecurity;

    public UserController(UserService userService, JwtUtil jwtUtil, TokenSecurityService tokenSecurity) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenSecurity = tokenSecurity;
    }

    @GetMapping("/info")
    public Result<UserInfoVO> info(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(userService.getCurrentUserInfo(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(Authentication authentication,
                                       @RequestBody Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateProfile(userId,
                body.getOrDefault("nickname", ""),
                body.getOrDefault("bio", ""),
                body.getOrDefault("avatar", ""));
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(Authentication authentication,
                                        @RequestBody Map<String, String> body,
                                        HttpServletRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updatePassword(userId,
                body.get("oldPassword"),
                body.get("newPassword"));
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) tokenSecurity.revoke(token, jwtUtil.getRemainingMillis(token));
        }
        return Result.success("密码修改成功", null);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) tokenSecurity.revoke(token, jwtUtil.getRemainingMillis(token));
        }
        return Result.success();
    }

}
