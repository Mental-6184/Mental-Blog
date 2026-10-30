package org.example.mentalblogh.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagVO {
    private Long id;
    private String name;
    private String slug;
    private Long articleCount;
    private String createdAt;
}
