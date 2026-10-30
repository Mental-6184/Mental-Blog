package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Notification;
import org.example.mentalblogh.mapper.NotificationMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationMapper mapper;
    public NotificationController(NotificationMapper mapper) { this.mapper = mapper; }

    @GetMapping
    public PageResult<Notification> list(@RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "20") long pageSize,
                                         Authentication auth) {
        Page<Notification> p = mapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, auth.getPrincipal())
                        .orderByDesc(Notification::getCreatedAt));
        return PageResult.success(p);
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount(Authentication auth) {
        return Result.success(mapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, auth.getPrincipal()).eq(Notification::getReadStatus, 0)));
    }

    @PutMapping("/{id}/read")
    public Result<Void> read(@PathVariable Long id, Authentication auth) {
        mapper.update(null, new LambdaUpdateWrapper<Notification>().set(Notification::getReadStatus, 1)
                .eq(Notification::getId, id).eq(Notification::getUserId, auth.getPrincipal()));
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> readAll(Authentication auth) {
        mapper.update(null, new LambdaUpdateWrapper<Notification>().set(Notification::getReadStatus, 1)
                .eq(Notification::getUserId, auth.getPrincipal()).eq(Notification::getReadStatus, 0));
        return Result.success();
    }
}
