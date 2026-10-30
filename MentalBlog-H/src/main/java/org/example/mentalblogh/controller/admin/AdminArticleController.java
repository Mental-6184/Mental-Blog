package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.request.ArticleRequest;
import org.example.mentalblogh.dto.response.ArticleVO;
import org.example.mentalblogh.service.ArticleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public PageResult<ArticleVO> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId) {
        IPage<ArticleVO> result = articleService.getAdminArticles(page, pageSize, status, categoryId);
        return PageResult.success(result);
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        // 管理端查看不增加阅读量，直接构造 VO
        return Result.success(articleService.getArticleById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody ArticleRequest request,
                                Authentication authentication) {
        Long authorId = (Long) authentication.getPrincipal();
        Long id = articleService.createArticle(request, authorId);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        articleService.updateArticle(id, request);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("删除成功", null);
    }
}
