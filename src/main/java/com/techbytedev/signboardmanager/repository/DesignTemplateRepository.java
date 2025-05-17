package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignTemplateRepository extends JpaRepository<DesignTemplate, Integer> {
    // List<DesignTemplate> findByCategoryAndIsActiveTrueAndDeletedAtIsNull(String category);

    List<DesignTemplate> findAllByDeletedAtIsNull();
    Optional<DesignTemplate> findByIdAndDeletedAtIsNull(Long id);
}