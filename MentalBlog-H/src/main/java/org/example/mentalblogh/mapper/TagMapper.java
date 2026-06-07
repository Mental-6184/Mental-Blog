package org.example.mentalblogh.mapper;

import org.example.mentalblogh.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT t.*, COUNT(at.article_id) as article_count " +
            "FROM tag t " +
            "LEFT JOIN article_tag at ON t.id = at.tag_id " +
            "GROUP BY t.id " +
            "ORDER BY article_count DESC, t.id ASC")
    List<Map<String, Object>> selectTagsWithCount();
}
