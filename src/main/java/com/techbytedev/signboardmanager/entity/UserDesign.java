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

    @Column(name = "design_name", length = 255, nullable = false)
    private String designName = "Unnamed Design"; // Gán giá trị mặc định

    @Column(name = "design_source", columnDefinition = "TEXT")
    private String designSource; // Lưu link Canva hoặc đường dẫn ảnh

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

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