package org.example.mentalblogh.mapper;

import org.example.mentalblogh.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    // 批量查询每篇文章的标签（用于列表页）
    @Select("SELECT at.article_id, t.name as tag_name FROM article_tag at " +
            "LEFT JOIN tag t ON at.tag_id = t.id " +
            "WHERE at.article_id IN (${articleIds})")
    List<Map<String, Object>> selectTagsByArticleIds(String articleIds);
}
