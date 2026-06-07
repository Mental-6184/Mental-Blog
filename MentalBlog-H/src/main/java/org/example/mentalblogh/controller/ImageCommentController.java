package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.ImageComment;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.ImageCommentMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/images")
public class ImageCommentController {

    private final ImageCommentMapper imageCommentMapper;
    private final UserMapper userMapper;

    public ImageCommentController(ImageCommentMapper imageCommentMapper, UserMapper userMapper) {
        this.imageCommentMapper = imageCommentMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/{imageId}/comments")
    public Result<List<Map<String, Object>>> listByImage(@PathVariable Long imageId) {
        List<ImageComment> all = imageCommentMapper.selectList(
                new LambdaQueryWrapper<ImageComment>()
                        .eq(ImageComment::getImageId, imageId)
                        .eq(ImageComment::getStatus, 1)
                        .orderByAsc(ImageComment::getCreatedAt));

        Map<Long, List<ImageComment>> childrenMap = new HashMap<>();
        for (ImageComment c : all) {
            if (c.getParentId() != null) {
                childrenMap.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ImageComment c : all) {
            if (c.getParentId() == null) {
                result.add(toMap(c, childrenMap));
            }
        }
        return Result.success(result);
    }

    private Map<String, Object> toMap(ImageComment c, Map<Long, List<ImageComment>> childrenMap) {
        User user = userMapper.selectById(c.getUserId());
        String name = user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "User";
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("content", c.getContent());
        m.put("userId", c.getUserId());
        m.put("userNickname", name);
        m.put("imageId", c.getImageId());
        m.put("parentId", c.getParentId());
        m.put("likeCount", c.getLikeCount());
        m.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);

        List<Map<String, Object>> children = new ArrayList<>();
        List<ImageComment> childList = childrenMap.get(c.getId());
        if (childList != null) {
            for (ImageComment child : childList) {
                children.add(toMap(child, childrenMap));
            }
        }
        m.put("children", children);
        return m;
    }

    @PostMapping("/{imageId}/comments")
    public Result<Void> create(@PathVariable Long imageId, @RequestBody Map<String, Object> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        ImageComment c = new ImageComment();
        c.setContent((String) body.get("content"));
        c.setUserId(userId);
        c.setImageId(imageId);
        Object pid = body.get("parentId");
        c.setParentId(pid != null ? ((Number) pid).longValue() : null);
        c.setStatus(1);
        c.setLikeCount(0);
        imageCommentMapper.insert(c);
        return Result.success("Comment posted", null);
    }

    @DeleteMapping("/{imageId}/comments/{id}")
    public Result<Void> delete(@PathVariable Long imageId, @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        ImageComment c = imageCommentMapper.selectById(id);
        if (c != null && c.getUserId().equals(userId) && c.getImageId().equals(imageId)) {
            imageCommentMapper.deleteById(id);
        }
        return Result.success();
    }
}
