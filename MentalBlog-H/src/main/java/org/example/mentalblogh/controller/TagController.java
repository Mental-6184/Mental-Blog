package org.example.mentalblogh.controller;

import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.dto.response.TagVO;
import org.example.mentalblogh.mapper.TagMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagMapper tagMapper;

    public TagController(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
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

    @GetMapping("/cloud")
    public Result<List<TagVO>> cloud() {
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

    private Long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        return 0L;
    }
}
