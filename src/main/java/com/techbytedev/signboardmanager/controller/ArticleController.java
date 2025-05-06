package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
