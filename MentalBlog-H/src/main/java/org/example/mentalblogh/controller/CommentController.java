package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.result.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.dto.request.CommentRequest;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.entity.Notification;
import org.example.mentalblogh.mapper.NotificationMapper;
import org.example.mentalblogh.mapper.CommentMapper;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
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
    private final ArticleMapper articleMapper;
    private final NotificationMapper notificationMapper;

    public CommentController(CommentMapper commentMapper, UserMapper userMapper, ArticleMapper articleMapper, NotificationMapper notificationMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.notificationMapper = notificationMapper;
    }

    /** 获取文章评论（树形结构） */
    @GetMapping("/article/{articleId}")
    public PageResult<Map<String, Object>> listByArticle(@PathVariable Long articleId,
                                                         @RequestParam(defaultValue = "1") long page,
                                                         @RequestParam(defaultValue = "20") long pageSize) {
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

        long from = Math.max(0, (page - 1) * pageSize);
        long to = Math.min(result.size(), from + pageSize);
        Page<Map<String, Object>> resultPage = new Page<>(page, pageSize, result.size());
        resultPage.setRecords(from >= result.size() ? List.of() : result.subList((int) from, (int) to));
        return PageResult.success(resultPage);
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
    @Transactional
    public Result<Void> create(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        org.example.mentalblogh.entity.Article article = articleMapper.selectById(request.getArticleId());
        if (article == null || article.getStatus() == null || article.getStatus() != 1) {
            return Result.error(404, "文章不存在或未发布");
        }
        if (request.getParentId() != null) {
            Comment parent = commentMapper.selectById(request.getParentId());
            if (parent == null || !request.getArticleId().equals(parent.getArticleId()) || parent.getStatus() != 1) {
                return Result.error(400, "回复目标不存在");
            }
        }
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setArticleId(request.getArticleId());
        comment.setParentId(request.getParentId());
        comment.setStatus(0);
        comment.setLikeCount(0);
        commentMapper.insert(comment);
        if (request.getParentId() != null) {
            Comment parent = commentMapper.selectById(request.getParentId());
            if (parent != null && !parent.getUserId().equals(userId)) {
                Notification n = new Notification(); n.setUserId(parent.getUserId()); n.setType("COMMENT_REPLY");
                n.setArticleId(request.getArticleId()); n.setCommentId(comment.getId()); n.setTitle("你的评论收到回复");
                n.setContent("有人回复了你的评论，审核通过后会显示在文章中。"); n.setReadStatus(0); notificationMapper.insert(n);
            }
        }
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Comment comment = commentMapper.selectById(id);
        if (comment != null && comment.getUserId().equals(userId)) {
            deleteTree(comment.getId(), comment.getArticleId());
        }
        return Result.success();
    }

    private void deleteTree(Long id, Long articleId) {
        List<Comment> children = commentMapper.selectList(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
        for (Comment child : children) deleteTree(child.getId(), articleId);
        Comment current = commentMapper.selectById(id);
        if (current != null && Integer.valueOf(1).equals(current.getStatus())) {
            com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<org.example.mentalblogh.entity.Article> uw =
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
            uw.setSql("comment_count = GREATEST(comment_count - 1, 0)")
                    .eq(org.example.mentalblogh.entity.Article::getId, articleId);
            articleMapper.update(null, uw);
        }
        commentMapper.deleteById(id);
    }
}
