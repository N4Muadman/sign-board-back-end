package com.techbytedev.signboardmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "api_path", length = 255)
    private String apiPath;

    @Column(length = 50)
    private String method;

    @Column(length = 100)
    private String module;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>(); // Khởi tạo để tránh NullPointerException

    @Override
    public String toString() {
        return "Permission{id=" + id + ", name='" + name + "', apiPath='" + apiPath + "', method='" + method + "', module='" + module + "'}";
    }
}