package com.techbytedev.signboardmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategoryResponseDTO {
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
    private List<ArticleCategoryResponseDTO> children;
}
