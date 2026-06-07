package org.example.mentalblogh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentVO {
    private Long id;
    private String content;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private Long articleId;
    private Long parentId;
    private Integer likeCount;
    private Integer status;
    private String createdAt;
    private List<CommentVO> children;
}
