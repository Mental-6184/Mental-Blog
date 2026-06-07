package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("image_apply")
public class ImageApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String reason;
    private Integer status; // 0-待审核, 1-已通过, 2-已拒绝
    private String reply;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
