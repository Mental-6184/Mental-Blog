package org.example.mentalblogh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArchiveVO {
    private String year;
    private String month;
    private List<ArchiveItem> articles;

    @Data
    @Builder
    public static class ArchiveItem {
        private Long id;
        private String title;
        private String slug;
        private String publishedAt;
    }
}
