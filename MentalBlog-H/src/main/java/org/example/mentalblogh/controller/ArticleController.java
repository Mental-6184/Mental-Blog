package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.response.ArchiveVO;
import org.example.mentalblogh.dto.response.ArticleVO;
import org.example.mentalblogh.service.ArticleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final StringRedisTemplate redis;

    public ArticleController(ArticleService articleService, StringRedisTemplate redis) {
        this.articleService = articleService;
        this.redis = redis;
    }

    @GetMapping
    public PageResult<ArticleVO> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        IPage<ArticleVO> result = articleService.getPublishedArticles(page, pageSize, categoryId, tagId, keyword);
        return PageResult.success(result);
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getPublishedArticleById(id));
    }

    @GetMapping("/slug/{slug}")
    public Result<ArticleVO> detailBySlug(@PathVariable String slug) {
        return Result.success(articleService.getPublishedArticleBySlug(slug));
    }

    @GetMapping("/archives")
    public Result<List<ArchiveVO>> archives() {
        return Result.success(articleService.getArchives());
    }

    /** 记录阅读量（前端调用，带防刷） */
    @PostMapping("/{id}/view")
    public Result<Void> recordView(@PathVariable Long id, HttpServletRequest request, Authentication authentication) {
        String identity = authentication != null ? "user:" + authentication.getPrincipal() : "ip:" + request.getRemoteAddr();
        try {
            String key = "article:view:" + id + ":" + sha256(identity);
            Boolean first = redis.opsForValue().setIfAbsent(key, "1", 24, TimeUnit.HOURS);
            if (Boolean.TRUE.equals(first)) articleService.incrementViewCount(id);
        } catch (RuntimeException ignored) {
            // Redis 不可用时仍允许访问，避免缓存故障影响前台。
            articleService.incrementViewCount(id);
        }
        return Result.success();
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            for (byte b : digest) out.append(String.format("%02x", b));
            return out.toString();
        } catch (Exception e) {
            return Integer.toHexString(value.hashCode());
        }
    }

    /** 热门文章列表 */
    @GetMapping("/hot")
    public Result<List<ArticleVO>> hot() {
        return Result.success(articleService.getHotArticles(5));
    }
}
