package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("image_tag")
public class ImageTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String slug;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
