package com.techbytedev.signboardmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedArticleResponse {
    private List<ArticleResponseDTO> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean last;
    private boolean first;

    public static PaginatedArticleResponse fromPage(Page<ArticleResponseDTO> page) {
        return PaginatedArticleResponse.builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
