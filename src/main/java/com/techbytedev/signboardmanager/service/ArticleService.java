package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.PostType;
import com.techbytedev.signboardmanager.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Article createArticleFromDTO(ArticleRequest dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSlug(dto.getSlug());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt());
        article.setFeatured(dto.isFeatured());

        try {
            article.setType(PostType.valueOf(dto.getType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid post type");
        }
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public void uploadImage(int id, MultipartFile file) throws IOException {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        String filePath = "src/main/resources/static/images/" + file.getOriginalFilename();
        File destFile = new File(filePath);

        if (!destFile.exists()) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                fos.write(file.getBytes());
            }
        }

        article.setFeaturedImageUrl(file.getOriginalFilename());
        articleRepository.save(article);
    }
    public Article updateArticle(int id, ArticleRequest dto) throws Exception {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        existingArticle.setTitle(dto.getTitle());
        existingArticle.setSlug(dto.getSlug());
        existingArticle.setContent(dto.getContent());
        existingArticle.setExcerpt(dto.getExcerpt());
        existingArticle.setFeatured(dto.isFeatured());

        try {
            existingArticle.setType(PostType.valueOf(dto.getType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid post type");
        }


        return articleRepository.save(existingArticle);
    }
    public void deleteArticle(int id) {
        articleRepository.deleteById(id);
    }
    public List<Article> searchArticles(String keyword) {
        return articleRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }
    public List<Article> getFeaturedProjects() {
        List<Article> articles = articleRepository.findByTypeAndIsFeaturedTrueOrderByCreatedAtDesc(PostType.project);

        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl("/images/" + article.getFeaturedImageUrl());
            }
        }

        return articles;
    }
    public List<Article> getProductionArticles() {
        List<Article> articles = articleRepository.findByType(PostType.production_info);

        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl("/images/" + article.getFeaturedImageUrl());
            }
        }

        return articles;
    }
    public List<Article> getNewsArticlesSortedByTime() {
        List<Article> articles = articleRepository.findByTypeOrderByCreatedAtDesc(PostType.news);

        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl("/images/" + article.getFeaturedImageUrl());
            }
        }

        return articles;
    }
}
