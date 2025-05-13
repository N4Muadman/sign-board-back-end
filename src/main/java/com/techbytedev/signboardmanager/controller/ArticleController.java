package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.service.ArticleService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // hiển thị các dự án tiêu biểu
    @GetMapping("/featured-projects")
    public ResponseEntity<?> getFeaturedProjects() {
        List<Article> articles = articleService.getFeaturedProjects();
        if(articles.isEmpty())
        {
            return ResponseEntity.ok("Không có dự án nào.");
        }
        return ResponseEntity.ok(articles);
    }
    // hiển thị trang sản xuất
    @GetMapping("/production")
    public ResponseEntity<?> getProductionArticles() {
        List<Article> articles = articleService.getProductionArticles();

        if (articles.isEmpty()) {
            return ResponseEntity.ok("Không có bài viết sản xuất nào.");
        }

        return ResponseEntity.ok(articles);
    }

     // hiển thị danh sách article
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getListArticle(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Article> articlePage = articleService.getAllArticles(pageable);

        List<Article> articles = articlePage.getContent();
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl("/images/" + article.getFeaturedImageUrl());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", articles);
        response.put("pageNumber", articlePage.getNumber() + 1);
        response.put("pageSize", articlePage.getSize());
        response.put("totalPages", articlePage.getTotalPages());
        response.put("totalElements", articlePage.getTotalElements());
        response.put("last", articlePage.isLast());

        return ResponseEntity.ok(response);
    }

    // hiển thị tin tức theo thời gian
    @GetMapping("/news")
    public ResponseEntity<?> getNewsArticlesSortedByTime() {
        List<Article> articles = articleService.getNewsArticlesSortedByTime();

        if (articles.isEmpty()) {
            return ResponseEntity.ok("Không có bài viết tin tức nào.");
        }

        return ResponseEntity.ok(articles);
    }
    // tìm kiếm tin tức
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(@RequestParam String keyword) {
        List<Article> articles = articleService.searchArticles(keyword);
        if (articles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(articles);
    }
}
