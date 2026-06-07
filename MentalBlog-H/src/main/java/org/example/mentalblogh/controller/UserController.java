package org.example.mentalblogh.controller;

import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.response.UserInfoVO;
import org.example.mentalblogh.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
                                        @RequestBody Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updatePassword(userId,
                body.get("oldPassword"),
                body.get("newPassword"));
        return Result.success("密码修改成功", null);
    }
}
