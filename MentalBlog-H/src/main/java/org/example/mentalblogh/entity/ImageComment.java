package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("image_comment")
public class ImageComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private Long userId;
    private Long imageId;
    private Long parentId;
    private Integer likeCount;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
