package com.techbytedev.signboardmanager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "article_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @JsonBackReference
    private ArticleCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ArticleCategory> children;

    @Column(name = "level")
    private int level;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (parentCategory != null) {
            level = parentCategory.getLevel() + 1;
            // Validate maximum depth (3 levels: 0, 1, 2)
            if (level > 2) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 3 levels are allowed (0-2).");
            }
        } else {
            level = 0;
        }
        if (sortOrder == 0) {
            sortOrder = 0; // Keep default sort order
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (parentCategory != null) {
            level = parentCategory.getLevel() + 1;
            // Validate maximum depth (3 levels: 0, 1, 2)
            if (level > 2) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 3 levels are allowed (0-2).");
            }
        } else {
            level = 0;
        }
        // sortOrder is managed manually, no need to change it here
    }
}
