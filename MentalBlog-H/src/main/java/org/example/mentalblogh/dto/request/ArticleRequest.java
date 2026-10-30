package org.example.mentalblogh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ArticleRequest {
    @NotBlank(message = "文章标题不能为空")
    private String title;

    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String coverImage;

    @NotBlank(message = "URL别名不能为空")
    private String slug;

    @NotNull(message = "请选择状态")
    private Integer status;

    private Integer top;

    private Long categoryId;

    private List<Long> tagIds;
}
