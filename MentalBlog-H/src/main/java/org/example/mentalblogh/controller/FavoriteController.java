package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.Favorite;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.FavoriteMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long articleId = body.get("articleId");

        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<>();
        query.eq(Favorite::getUserId, userId).eq(Favorite::getArticleId, articleId);

        Favorite existing = favoriteMapper.selectOne(query);
        boolean favorited;
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            favorited = false;
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.setSql("favorite_count = favorite_count - 1").eq(Article::getId, articleId);
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
    public Result<List<Long>> myFavorites(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        List<Long> ids = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId)
        ).stream().map(Favorite::getArticleId).collect(Collectors.toList());
        return Result.success(ids);
    }
}
