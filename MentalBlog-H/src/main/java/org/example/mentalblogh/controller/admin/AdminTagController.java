package org.example.mentalblogh.controller.admin;

import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.utils.CacheUtil;
import org.example.mentalblogh.dto.response.TagVO;
import org.example.mentalblogh.entity.Tag;
import org.example.mentalblogh.mapper.TagMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tags")
public class AdminTagController {

    private final TagMapper tagMapper;
    private final CacheUtil cacheUtil;

    public AdminTagController(TagMapper tagMapper, CacheUtil cacheUtil) {
        this.tagMapper = tagMapper;
        this.cacheUtil = cacheUtil;
    }

    @GetMapping
    public Result<List<TagVO>> list() {
        List<Map<String, Object>> rows = tagMapper.selectTagsWithCount();
        List<TagVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            list.add(TagVO.builder()
                .id(toLong(row.get("id")))
                .name((String) row.get("name"))
                .slug((String) row.get("slug"))
                .articleCount(toLong(row.get("article_count")))
                .createdAt(row.get("created_at") != null ? row.get("created_at").toString() : null)
                .build());
        }
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Tag tag) {
        tagMapper.insert(tag);
        cacheUtil.delete("cache:tags");
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        tagMapper.updateById(tag);
        cacheUtil.delete("cache:tags");
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagMapper.deleteById(id);
        cacheUtil.delete("cache:tags");
        return Result.success("删除成功", null);
    }

    private Long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        return 0L;
    }
}
