package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.response.ArchiveVO;
import org.example.mentalblogh.dto.response.ArticleVO;
import org.example.mentalblogh.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
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
        return Result.success(articleService.getArticleById(id));
    }

    @GetMapping("/slug/{slug}")
    public Result<ArticleVO> detailBySlug(@PathVariable String slug) {
        return Result.success(articleService.getArticleBySlug(slug));
    }

    @GetMapping("/archives")
    public Result<List<ArchiveVO>> archives() {
        return Result.success(articleService.getArchives());
    }

    /** 记录阅读量（前端调用，带防刷） */
    @PostMapping("/{id}/view")
    public Result<Void> recordView(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return Result.success();
    }

    /** 热门文章列表 */
    @GetMapping("/hot")
    public Result<List<ArticleVO>> hot() {
        return Result.success(articleService.getHotArticles(5));
    }
}
