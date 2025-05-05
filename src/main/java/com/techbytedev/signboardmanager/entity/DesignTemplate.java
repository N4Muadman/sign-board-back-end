package com.techbytedev.signboardmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "design_templates")
@Data
public class DesignTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "preview_image_url", nullable = false, columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column(name = "suggested_dimensions", length = 100)
    private String suggestedDimensions;

    @Column(name = "canva_template_link", columnDefinition = "TEXT")
    private String canvaTemplateLink;

    @Column(length = 100)
    private String category;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}