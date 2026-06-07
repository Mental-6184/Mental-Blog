package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.exception.BusinessException;
import org.example.mentalblogh.common.result.ErrorCode;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.utils.CacheUtil;
import org.example.mentalblogh.entity.Category;
import org.example.mentalblogh.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;
    private final CacheUtil cacheUtil;

    public AdminCategoryController(CategoryMapper categoryMapper, CacheUtil cacheUtil) {
        this.categoryMapper = categoryMapper;
        this.cacheUtil = cacheUtil;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        categoryMapper.insert(category);
        cacheUtil.delete("cache:categories");
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
        cacheUtil.delete("cache:categories");
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        cacheUtil.delete("cache:categories");
        return Result.success("删除成功", null);
    }
}
