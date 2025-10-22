package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByTitleContainingOrContentContaining(String title, String content);
    List<Article> findByTypeAndIsFeaturedTrueOrderByCreatedAtDesc(PostType type);
    List<Article> findByType(PostType type);
    List<Article> findByTypeOrderByCreatedAtDesc(PostType type);
    Page<Article> findAllByTypeOrderByCreatedAtDesc(Pageable pageable, PostType type);
    List<Article> findByCategoryIdOrderByCreatedAtDesc(int categoryId);
    List<Article> findByCategoryId(int categoryId);
    @Query("SELECT a FROM Article a WHERE a.category.id IN :categoryIds ORDER BY a.createdAt DESC")
    Page<Article> findByCategoryIdIn(@Param("categoryIds") Collection<Integer> categoryIds, Pageable pageable);

    Article findBySlug(String slug);
    @Query("SELECT a FROM Article a WHERE a.category.id IN :categoryIds AND (a.title LIKE %:search% OR a.content LIKE %:search%) ORDER BY a.createdAt DESC")
    Page<Article> findByCategoryIdInAndSearch(@Param("categoryIds") Collection<Integer> categoryIds, @Param("search") String search, Pageable pageable);
}
