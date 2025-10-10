package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Integer> {

    List<ArticleCategory> findByIsActiveTrue();

    List<ArticleCategory> findByParentCategoryIsNullAndIsActiveTrue();

    List<ArticleCategory> findByParentCategoryIdAndIsActiveTrue(int parentId);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, int id);

    @Query("SELECT ac FROM ArticleCategory ac WHERE ac.parentCategory IS NULL AND ac.isActive = true ORDER BY ac.sortOrder, ac.name")
    List<ArticleCategory> findRootCategoriesOrderedBySortOrder();

    @Query("SELECT ac FROM ArticleCategory ac WHERE ac.level > 0 AND ac.isActive = true ORDER BY ac.level, ac.parentCategory.id, ac.sortOrder, ac.name")
    List<ArticleCategory> findChildCategoriesOrdered();

    ArticleCategory findBySlug(String slug);

    ArticleCategory findBySlugAndIsActiveTrue(String slug);
}
