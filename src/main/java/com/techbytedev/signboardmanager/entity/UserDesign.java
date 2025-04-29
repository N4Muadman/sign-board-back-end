package com.techbytedev.signboardmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_designs")
@Data
public class UserDesign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "design_name", length = 255)
    private String designName;

    @Column(name = "canva_design_id", nullable = false, unique = true, length = 255)
    private String canvaDesignId;

    @Column(name = "canva_preview_url", columnDefinition = "TEXT")
    private String canvaPreviewUrl;

    @Column(name = "canva_export_link", columnDefinition = "TEXT")
    private String canvaExportLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum Status {
        DRAFT, SUBMITTED, PROCESSING, QUOTED, REVISION_NEEDED, APPROVED, COMPLETED, CANCELLED
    }
}