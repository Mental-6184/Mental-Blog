package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.ImageApply;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.ImageApplyMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/image-permission")
public class ImagePermissionController {

    private final UserMapper userMapper;
    private final ImageApplyMapper imageApplyMapper;

    public ImagePermissionController(UserMapper userMapper, ImageApplyMapper imageApplyMapper) {
        this.userMapper = userMapper;
        this.imageApplyMapper = imageApplyMapper;
    }

    // ===== 检查当前用户的图片权限 =====
    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error(401, "用户不存在");

        Map<String, Object> data = new HashMap<>();
        data.put("imagePermission", user.getImagePermission() != null ? user.getImagePermission() : 0);

        // 如果有待审核的申请，返回申请信息
        ImageApply apply = imageApplyMapper.selectOne(
                new LambdaQueryWrapper<ImageApply>()
                        .eq(ImageApply::getUserId, userId)
                        .orderByDesc(ImageApply::getCreatedAt)
                        .last("LIMIT 1"));
        data.put("apply", apply);

        return Result.success(data);
    }

    // ===== 提交申请 =====
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody Map<String, String> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error(401, "用户不存在");

        // 检查是否已有待审核记录
        long pending = imageApplyMapper.selectCount(
                new LambdaQueryWrapper<ImageApply>()
                        .eq(ImageApply::getUserId, userId)
                        .eq(ImageApply::getStatus, 0));
        if (pending > 0) {
            return Result.error(400, "已有待审核的申请，请耐心等待");
        }

        ImageApply apply = new ImageApply();
        apply.setUserId(userId);
        apply.setReason(body.getOrDefault("reason", ""));
        apply.setStatus(0);
        imageApplyMapper.insert(apply);

        // 更新用户状态为待审核
        user.setImagePermission(1);
        userMapper.updateById(user);

        return Result.success("申请已提交", null);
    }
}
