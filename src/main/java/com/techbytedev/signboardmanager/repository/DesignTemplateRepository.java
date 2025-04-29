package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignTemplateRepository extends JpaRepository<DesignTemplate, Integer> {
    List<DesignTemplate> findByCategoryAndIsActiveTrueAndDeletedAtIsNull(String category);
}