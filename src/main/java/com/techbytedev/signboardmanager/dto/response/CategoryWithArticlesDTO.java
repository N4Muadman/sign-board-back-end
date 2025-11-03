package com.techbytedev.signboardmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryWithArticlesDTO {
    // Category information
    private int id;
    private String name;
    private String slug;
    private String description;
    private boolean isActive;
    private Integer parentId;
    private String parentName;
    private int level;
    private int sortOrder;
    private int articleCount;
    private int childrenCount;
    private int totalChildrenArticlesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Articles list (paged)
    private List<ArticleResponseDTO> articles;
    private List<CategoryWithArticlesDTO> subcategories;
    
    // Pagination information
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    
    // Helper method to check if there are more pages
    public boolean hasNext() {
        return currentPage < totalPages - 1;
    }
    
    // Helper method to check if this is the first page
    public boolean isFirst() {
        return currentPage == 0;
    }
    
    // Helper method to check if this is the last page
    public boolean isLast() {
        return currentPage >= totalPages - 1;
    }
    
    // Helper method to get the next page number
    public int getNextPage() {
        return hasNext() ? currentPage + 1 : currentPage;
    }
    
    // Helper method to get the previous page number
    public int getPreviousPage() {
        return isFirst() ? 0 : currentPage - 1;
    }
}
