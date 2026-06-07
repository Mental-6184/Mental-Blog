package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friend_link")
public class FriendLink {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String url;

    private String logo;

    private String description;

    private Integer sort;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
