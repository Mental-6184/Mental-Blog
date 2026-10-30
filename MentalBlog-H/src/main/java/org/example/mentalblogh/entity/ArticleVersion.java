package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("article_version")
public class ArticleVersion {
    @TableId(type = IdType.AUTO) private Long id;
    private Long articleId;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String slug;
    private Integer status;
    private Integer top;
    private Long categoryId;
    private String tagIds;
    private Long authorId;
    private String note;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
}
