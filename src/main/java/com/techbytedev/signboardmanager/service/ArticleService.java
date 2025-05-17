package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.PostType;
import com.techbytedev.signboardmanager.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FileStorageService fileStorageService;

    public ArticleService(ArticleRepository articleRepository, FileStorageService fileStorageService) {
        this.articleRepository = articleRepository;
        this.fileStorageService = fileStorageService;
    }

    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Article createArticleFromDTO(ArticleRequest dto, MultipartFile imageFile) throws IOException {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSlug(dto.getSlug());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt());
        article.setFeatured(dto.isFeatured());

        try {
            article.setType(PostType.valueOf(dto.getType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            article.setFeaturedImageUrl(fileName);
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Article updateArticle(int id, ArticleRequest dto, MultipartFile imageFile) throws IOException {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        existingArticle.setTitle(dto.getTitle());
        existingArticle.setSlug(dto.getSlug());
        existingArticle.setContent(dto.getContent());
        existingArticle.setExcerpt(dto.getExcerpt());
        existingArticle.setFeatured(dto.isFeatured());

        try {
            existingArticle.setType(PostType.valueOf(dto.getType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            // Xóa ảnh cũ nếu có
            if (existingArticle.getFeaturedImageUrl() != null) {
                fileStorageService.deleteFile(existingArticle.getFeaturedImageUrl());
            }
            // Lưu ảnh mới
            String fileName = fileStorageService.saveFile(imageFile);
            existingArticle.setFeaturedImageUrl(fileName);
        }

        existingArticle.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(existingArticle);
    }

    public void deleteArticle(int id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        // Xóa ảnh nếu có
        if (article.getFeaturedImageUrl() != null) {
            try {
                fileStorageService.deleteFile(article.getFeaturedImageUrl());
            } catch (IOException e) {
                // Log lỗi nhưng không làm gián đoạn
            }
        }

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