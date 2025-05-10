package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByTitleContainingOrContentContaining(String title, String content);
    List<Article> findByTypeAndIsFeaturedTrueOrderByCreatedAtDesc(PostType type);
    List<Article> findByType(PostType type);
    List<Article> findByTypeOrderByCreatedAtDesc(PostType type);
    Page<Article> findAll(Pageable pageable);
}
