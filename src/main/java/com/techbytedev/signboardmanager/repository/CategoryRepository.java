package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentCategoryIdIsNull();
    long countByParentCategoryId(int parentCategoryId);
    List<Category> findByNameContainingIgnoreCase(String name);
    Page<Category> findAll(Pageable pageable);
    List<Category> findByParentCategoryId(int parentCategoryId);
}
