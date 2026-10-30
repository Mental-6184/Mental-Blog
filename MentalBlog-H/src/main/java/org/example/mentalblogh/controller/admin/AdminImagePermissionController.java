package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.ImageApply;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.ImageApplyMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/image-permission")
public class AdminImagePermissionController {

    private final UserMapper userMapper;
    private final ImageApplyMapper imageApplyMapper;

    public AdminImagePermissionController(UserMapper userMapper, ImageApplyMapper imageApplyMapper) {
        this.userMapper = userMapper;
        this.imageApplyMapper = imageApplyMapper;
    }

    // ===== 获取所有申请（含申请人信息） =====
    @GetMapping
    public Result<List<Map<String, Object>>> getApplies() {
        List<ImageApply> applies = imageApplyMapper.selectList(
                new LambdaQueryWrapper<ImageApply>().orderByDesc(ImageApply::getCreatedAt));

        List<Map<String, Object>> result = applies.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("userId", a.getUserId());
            m.put("reason", a.getReason());
            m.put("status", a.getStatus());
            m.put("reply", a.getReply());
            m.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);

            User u = userMapper.selectById(a.getUserId());
            m.put("username", u != null ? u.getUsername() : "未知");
            m.put("nickname", u != null ? u.getNickname() : "未知");

            return m;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    // ===== 审核 =====
    @PutMapping("/{id}")
    public Result<Void> review(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        int newStatus = (int) body.get("status"); // 1-通过, 2-拒绝
        String reply = (String) body.getOrDefault("reply", "");

        ImageApply apply = imageApplyMapper.selectById(id);
        if (apply == null) return Result.error(404, "申请不存在");

        apply.setStatus(newStatus);
        apply.setReply(reply);
        imageApplyMapper.updateById(apply);

        // 更新用户权限
        User user = userMapper.selectById(apply.getUserId());
        if (user != null) {
            user.setImagePermission(newStatus == 1 ? 2 : 3);
            userMapper.updateById(user);
        }

        return Result.success(newStatus == 1 ? "已通过" : "已拒绝", null);
    }

    // ===== 直接设置用户图片权限（开通/剥夺） =====
    @PutMapping("/user/{userId}")
    public Result<Void> setUserPermission(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Object raw = body.get("imagePermission");
        int permission = raw instanceof Number ? ((Number) raw).intValue() : 0;
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        user.setImagePermission(permission);
        userMapper.updateById(user);
        return Result.success(permission == 2 ? "已开通" : "已剥夺", null);
    }
}
