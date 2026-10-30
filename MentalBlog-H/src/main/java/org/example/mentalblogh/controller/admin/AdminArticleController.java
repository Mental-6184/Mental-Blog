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
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.ArticleVersion;
import org.example.mentalblogh.mapper.ArticleVersionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;
    private final ArticleVersionMapper versionMapper;

    public AdminArticleController(ArticleService articleService, ArticleVersionMapper versionMapper) {
        this.articleService = articleService;
        this.versionMapper = versionMapper;
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

    @PostMapping("/{id}/autosave")
    public Result<Long> autosave(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        request.setStatus(0);
        if (id == 0) return Result.success("请先手动保存以创建草稿", null);
        articleService.updateArticle(id, request);
        return Result.success("草稿已自动保存", id);
    }

    @GetMapping("/{id}/versions")
    public Result<List<ArticleVersion>> versions(@PathVariable Long id) {
        return Result.success(versionMapper.selectList(new LambdaQueryWrapper<ArticleVersion>()
                .eq(ArticleVersion::getArticleId, id).orderByDesc(ArticleVersion::getCreatedAt)));
    }

    @GetMapping("/{id}/versions/{versionId}")
    public Result<ArticleVersion> version(@PathVariable Long id, @PathVariable Long versionId) {
        ArticleVersion version = versionMapper.selectOne(new LambdaQueryWrapper<ArticleVersion>()
                .eq(ArticleVersion::getId, versionId).eq(ArticleVersion::getArticleId, id));
        return version == null ? Result.error(404, "版本不存在") : Result.success(version);
    }
}
