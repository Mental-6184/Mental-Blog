package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.Favorite;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.FavoriteMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;

    public FavoriteController(FavoriteMapper favoriteMapper, ArticleMapper articleMapper) {
        this.favoriteMapper = favoriteMapper;
        this.articleMapper = articleMapper;
    }

    /** 收藏/取消收藏文章 */
    @PostMapping
    @Transactional
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long articleId = body.get("articleId");
        Article article = articleMapper.selectById(articleId);
        if (article == null || !Integer.valueOf(1).equals(article.getStatus())) return Result.error(404, "文章不存在");

        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<>();
        query.eq(Favorite::getUserId, userId).eq(Favorite::getArticleId, articleId);

        Favorite existing = favoriteMapper.selectOne(query);
        boolean favorited;
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            favorited = false;
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.setSql("favorite_count = GREATEST(favorite_count - 1, 0)").eq(Article::getId, articleId);
            articleMapper.update(null, uw);
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setArticleId(articleId);
            favoriteMapper.insert(fav);
            favorited = true;
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.setSql("favorite_count = favorite_count + 1").eq(Article::getId, articleId);
            articleMapper.update(null, uw);
        }

        return Result.success(Map.of("favorited", favorited));
    }

    /** 检查当前用户是否收藏了某文章 */
    @GetMapping("/check/{articleId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long articleId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<>();
        query.eq(Favorite::getUserId, userId).eq(Favorite::getArticleId, articleId);
        boolean favorited = favoriteMapper.selectCount(query) > 0;
        return Result.success(Map.of("favorited", favorited));
    }

    /** 当前用户的收藏列表 */
    @GetMapping
    public PageResult<Map<String, Object>> myFavorites(@RequestParam(defaultValue = "1") long page,
                                                       @RequestParam(defaultValue = "20") long pageSize,
                                                       Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Page<Favorite> favorites = favoriteMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreatedAt));
        Page<Map<String, Object>> result = new Page<>(page, pageSize, favorites.getTotal());
        result.setRecords(favorites.getRecords().stream().map(f -> {
            Article a = articleMapper.selectById(f.getArticleId());
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("favoriteId", f.getId()); m.put("articleId", f.getArticleId()); m.put("favoriteCreatedAt", f.getCreatedAt());
            m.put("available", a != null && Integer.valueOf(1).equals(a.getStatus()));
            if (a != null) { m.put("id", a.getId()); m.put("title", a.getTitle()); m.put("summary", a.getSummary()); m.put("publishedAt", a.getPublishedAt()); m.put("categoryId", a.getCategoryId()); }
            return m;
        }).collect(Collectors.toList()));
        return PageResult.success(result);
    }
}
