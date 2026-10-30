package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.mapper.CommentMapper;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.LikeRecordMapper;
import org.example.mentalblogh.mapper.NotificationMapper;
import org.example.mentalblogh.entity.Notification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final NotificationMapper notificationMapper;

    public AdminCommentController(CommentMapper commentMapper, ArticleMapper articleMapper, LikeRecordMapper likeRecordMapper, NotificationMapper notificationMapper) {
        this.commentMapper = commentMapper;
        this.articleMapper = articleMapper;
        this.likeRecordMapper = likeRecordMapper;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public PageResult<Comment> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Comment> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
        if (status != null) {
            query.eq(Comment::getStatus, status);
        }
        query.orderByDesc(Comment::getCreatedAt);
        return PageResult.success(commentMapper.selectPage(pageParam, query));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) deleteTree(comment.getId(), comment.getArticleId());
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    @Transactional
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            Integer oldStatus = comment.getStatus();
            Integer newStatus = body.get("status");
            if (newStatus == null || newStatus < 0 || newStatus > 2) return Result.error(400, "评论状态无效");
            comment.setStatus(body.get("status"));
            commentMapper.updateById(comment);
            if (!Integer.valueOf(1).equals(oldStatus) && Integer.valueOf(1).equals(newStatus)) adjustCount(comment.getArticleId(), 1);
            if (Integer.valueOf(1).equals(oldStatus) && !Integer.valueOf(1).equals(newStatus)) adjustCount(comment.getArticleId(), -1);
            if (!Integer.valueOf(1).equals(oldStatus) && Integer.valueOf(1).equals(newStatus)) {
                Notification n = new Notification(); n.setUserId(comment.getUserId()); n.setType("COMMENT_APPROVED");
                n.setArticleId(comment.getArticleId()); n.setCommentId(comment.getId()); n.setTitle("评论审核通过");
                n.setContent("你的评论已经通过审核。"); n.setReadStatus(0); notificationMapper.insert(n);
            }
            if (Integer.valueOf(1).equals(newStatus) || Integer.valueOf(2).equals(newStatus)) {
                if (Integer.valueOf(2).equals(newStatus)) {
                    Notification n = new Notification(); n.setUserId(comment.getUserId()); n.setType("COMMENT_REJECTED");
                    n.setArticleId(comment.getArticleId()); n.setCommentId(comment.getId()); n.setTitle("评论未通过审核");
                    n.setContent("你的评论未通过审核。"); n.setReadStatus(0); notificationMapper.insert(n);
                }
            }
        }
        return Result.success();
    }

    private void adjustCount(Long articleId, int delta) {
        String sql = delta > 0 ? "comment_count = comment_count + 1" : "comment_count = GREATEST(comment_count - 1, 0)";
        articleMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<org.example.mentalblogh.entity.Article>()
                .setSql(sql).eq(org.example.mentalblogh.entity.Article::getId, articleId));
    }

    private void deleteTree(Long id, Long articleId) {
        for (Comment child : commentMapper.selectList(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id))) {
            deleteTree(child.getId(), articleId);
        }
        Comment current = commentMapper.selectById(id);
        if (current != null && Integer.valueOf(1).equals(current.getStatus())) adjustCount(articleId, -1);
        likeRecordMapper.delete(new LambdaQueryWrapper<org.example.mentalblogh.entity.LikeRecord>()
                .eq(org.example.mentalblogh.entity.LikeRecord::getCommentId, id));
        commentMapper.deleteById(id);
    }
}
