package org.example.mentalblogh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("image_tag_rel")
public class ImageTagRel {
    private Long imageId;
    private Long tagId;
}
