package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.Category;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    public CategoryController(CategoryMapper categoryMapper, ArticleMapper articleMapper) {
        this.categoryMapper = categoryMapper;
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));

        // 统计每分类的文章数（已发布）
        Map<Long, Long> countMap = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .isNotNull(Article::getCategoryId)
        ).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategoryId() != null ? a.getCategoryId() : 0L,
                        Collectors.counting()
                ));

        // 统计总阅读量（每分类下已发布文章的 view_count 之和）
        Map<Long, Long> viewMap = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .isNotNull(Article::getCategoryId)
        ).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategoryId() != null ? a.getCategoryId() : 0L,
                        Collectors.summingLong(a -> a.getViewCount() != null ? a.getViewCount() : 0L)
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : categories) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", cat.getId());
            m.put("name", cat.getName());
            m.put("slug", cat.getSlug());
            m.put("description", cat.getDescription());
            m.put("sort", cat.getSort());
            m.put("createdAt", cat.getCreatedAt());
            m.put("count", countMap.getOrDefault(cat.getId(), 0L));
            m.put("viewCount", viewMap.getOrDefault(cat.getId(), 0L));
            result.add(m);
        }

        return Result.success(result);
    }
}
