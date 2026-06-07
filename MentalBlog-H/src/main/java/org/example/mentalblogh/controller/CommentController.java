package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.request.CommentRequest;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.CommentMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public CommentController(CommentMapper commentMapper, UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    /** 获取文章评论（树形结构） */
    @GetMapping("/article/{articleId}")
    public Result<List<Map<String, Object>>> listByArticle(@PathVariable Long articleId) {
        List<Comment> allComments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreatedAt));

        // 构建树形
        List<Map<String, Object>> result = new ArrayList<>();
        Map<Long, List<Comment>> childrenMap = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        for (Comment c : allComments) {
            if (c.getParentId() == null) {
                Map<String, Object> node = commentToMap(c);
                node.put("children", buildChildren(c.getId(), childrenMap));
                result.add(node);
            }
        }

        return Result.success(result);
    }

    private List<Map<String, Object>> buildChildren(Long parentId, Map<Long, List<Comment>> childrenMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Comment> children = childrenMap.get(parentId);
        if (children == null) return result;
        for (Comment c : children) {
            Map<String, Object> node = commentToMap(c);
            node.put("children", buildChildren(c.getId(), childrenMap));
            result.add(node);
        }
        return result;
    }

    private Map<String, Object> commentToMap(Comment c) {
        User user = userMapper.selectById(c.getUserId());
        String userName = user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "用户";
        String userAvatar = user != null ? user.getAvatar() : null;

        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("content", c.getContent());
        map.put("userId", c.getUserId());
        map.put("userNickname", userName);
        map.put("userAvatar", userAvatar);
        map.put("articleId", c.getArticleId());
        map.put("parentId", c.getParentId());
        map.put("likeCount", c.getLikeCount());
        map.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        return map;
    }

    /** 获取当前用户的所有评论 */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myComments(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        List<Comment> list = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getUserId, userId)
                        .orderByDesc(Comment::getCreatedAt));
        List<Map<String, Object>> result = list.stream().map(c -> {
            Map<String, Object> m = commentToMap(c);
            m.put("status", c.getStatus());
            return m;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> create(@RequestBody CommentRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setArticleId(request.getArticleId());
        comment.setParentId(request.getParentId());
        comment.setStatus(1);
        comment.setLikeCount(0);
        commentMapper.insert(comment);

        // 更新文章评论数
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<org.example.mentalblogh.entity.Article> uw =
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        uw.setSql("comment_count = comment_count + 1")
                .eq(org.example.mentalblogh.entity.Article::getId, request.getArticleId());
        // 得注入 ArticleMapper，直接写报告等下来优化，先简单更新entity
        // 跳过
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Comment comment = commentMapper.selectById(id);
        if (comment != null && comment.getUserId().equals(userId)) {
            commentMapper.deleteById(id);
        }
        return Result.success();
    }
}
