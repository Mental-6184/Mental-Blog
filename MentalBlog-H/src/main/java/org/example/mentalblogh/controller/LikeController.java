package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.entity.LikeRecord;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.CommentMapper;
import org.example.mentalblogh.mapper.LikeRecordMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeRecordMapper likeRecordMapper;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    public LikeController(LikeRecordMapper likeRecordMapper, ArticleMapper articleMapper, CommentMapper commentMapper) {
        this.likeRecordMapper = likeRecordMapper;
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
    }

    /** 点赞/取消点赞文章 */
    @PostMapping("/article")
    public Result<Map<String, Object>> toggleArticle(@RequestBody Map<String, Long> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long articleId = body.get("articleId");

        LambdaQueryWrapper<LikeRecord> query = new LambdaQueryWrapper<>();
        query.eq(LikeRecord::getUserId, userId).eq(LikeRecord::getArticleId, articleId);

        LikeRecord existing = likeRecordMapper.selectOne(query);
        boolean liked;
        if (existing != null) {
            likeRecordMapper.deleteById(existing.getId());
            liked = false;
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.setSql("like_count = like_count - 1").eq(Article::getId, articleId);
            articleMapper.update(null, uw);
        } else {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setArticleId(articleId);
            likeRecordMapper.insert(record);
            liked = true;
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.setSql("like_count = like_count + 1").eq(Article::getId, articleId);
            articleMapper.update(null, uw);
        }

        return Result.success(Map.of("liked", liked));
    }

    /** 点赞/取消点赞评论 */
    @PostMapping("/comment")
    public Result<Map<String, Object>> toggleComment(@RequestBody Map<String, Long> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long commentId = body.get("commentId");

        LambdaQueryWrapper<LikeRecord> query = new LambdaQueryWrapper<>();
        query.eq(LikeRecord::getUserId, userId).eq(LikeRecord::getCommentId, commentId);

        LikeRecord existing = likeRecordMapper.selectOne(query);
        boolean liked;
        if (existing != null) {
            likeRecordMapper.deleteById(existing.getId());
            liked = false;
            LambdaUpdateWrapper<Comment> uw = new LambdaUpdateWrapper<>();
            uw.setSql("like_count = like_count - 1").eq(Comment::getId, commentId);
            commentMapper.update(null, uw);
        } else {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setCommentId(commentId);
            likeRecordMapper.insert(record);
            liked = true;
            LambdaUpdateWrapper<Comment> uw = new LambdaUpdateWrapper<>();
            uw.setSql("like_count = like_count + 1").eq(Comment::getId, commentId);
            commentMapper.update(null, uw);
        }

        return Result.success(Map.of("liked", liked));
    }

    /** 检查当前用户是否点赞了某文章 */
    @GetMapping("/article/check/{articleId}")
    public Result<Map<String, Boolean>> checkArticle(@PathVariable Long articleId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        LambdaQueryWrapper<LikeRecord> query = new LambdaQueryWrapper<>();
        query.eq(LikeRecord::getUserId, userId).eq(LikeRecord::getArticleId, articleId);
        boolean liked = likeRecordMapper.selectCount(query) > 0;
        return Result.success(Map.of("liked", liked));
    }
}
