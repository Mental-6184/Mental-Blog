package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.*;
import org.example.mentalblogh.mapper.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final FavoriteMapper favoriteMapper;

    public AdminDashboardController(ArticleMapper articleMapper, CommentMapper commentMapper,
                                     UserMapper userMapper, CategoryMapper categoryMapper,
                                     TagMapper tagMapper, LikeRecordMapper likeRecordMapper,
                                     FavoriteMapper favoriteMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.likeRecordMapper = likeRecordMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @GetMapping
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        // 基础计数
        long articleCount = articleMapper.selectCount(null);
        data.put("articleCount", articleCount);
        data.put("commentCount", commentMapper.selectCount(null));
        data.put("userCount", userMapper.selectCount(null));
        data.put("categoryCount", categoryMapper.selectCount(null));
        data.put("tagCount", tagMapper.selectCount(null));

        // 草稿数
        long draftCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 0));
        data.put("draftCount", draftCount);

        // 总阅读量
        List<Article> allArticles = articleMapper.selectList(null);
        long totalViews = allArticles.stream().mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0).sum();
        data.put("totalViews", totalViews);

        // 今日新增
        LocalDate today = LocalDate.now();
        long todayArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().ge(Article::getCreatedAt, today.atStartOfDay()));
        data.put("todayArticles", todayArticles);
        long todayComments = commentMapper.selectCount(
                new LambdaQueryWrapper<org.example.mentalblogh.entity.Comment>().ge(Comment::getCreatedAt, today.atStartOfDay()));
        data.put("todayComments", todayComments);

        // 总互动数（点赞+收藏）
        long totalLikes = likeRecordMapper.selectCount(null);
        long totalFavorites = favoriteMapper.selectCount(null);
        data.put("totalInteractions", totalLikes + totalFavorites);

        // 最近30天文章发布趋势
        List<Map<String, Object>> trends = getArticleTrends();
        data.put("trends", trends);

        // 各分类文章数
        List<Map<String, Object>> categoryStats = getCategoryStats();
        data.put("categoryStats", categoryStats);

        // 最近评论（5条）
        List<Map<String, Object>> recentComments = getRecentComments();
        data.put("recentComments", recentComments);

        // 最近文章（5篇）
        List<Map<String, Object>> recentArticles = getRecentArticles();
        data.put("recentArticles", recentArticles);

        return Result.success(data);
    }

    private List<Map<String, Object>> getArticleTrends() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        List<Map<String, Object>> result = new ArrayList<>();
        Map<LocalDate, Long> countMap = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .isNotNull(Article::getPublishedAt)
                        .ge(Article::getPublishedAt, start.atStartOfDay())
        ).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPublishedAt().toLocalDate(),
                        Collectors.counting()
                ));

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", countMap.getOrDefault(date, 0L));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> getCategoryStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        // 用 MyBatis-Plus 做分类统计
        List<Article> all = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .isNotNull(Article::getCategoryId));
        Map<Long, Long> group = all.stream()
                .collect(Collectors.groupingBy(Article::getCategoryId, Collectors.counting()));

        group.forEach((catId, count) -> {
            String name = categoryMapper.selectById(catId) != null
                    ? categoryMapper.selectById(catId).getName() : "未分类";
            result.add(Map.of("name", name, "count", count));
        });
        return result;
    }

    private List<Map<String, Object>> getRecentComments() {
        return commentMapper.selectList(
                new LambdaQueryWrapper<org.example.mentalblogh.entity.Comment>()
                        .orderByDesc(org.example.mentalblogh.entity.Comment::getCreatedAt)
                        .last("LIMIT 5")
        ).stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("content", c.getContent().length() > 50
                    ? c.getContent().substring(0, 50) + "..."
                    : c.getContent());
            m.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            m.put("articleId", c.getArticleId());
            return m;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getRecentArticles() {
        return articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .orderByDesc(Article::getCreatedAt)
                        .last("LIMIT 5")
        ).stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("title", a.getTitle().length() > 30
                    ? a.getTitle().substring(0, 30) + "..."
                    : a.getTitle());
            m.put("status", a.getStatus());
            m.put("viewCount", a.getViewCount());
            m.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
            return m;
        }).collect(Collectors.toList());
    }
}
