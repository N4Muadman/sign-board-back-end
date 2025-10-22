package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.ArticleCategoryResponseDTO;
import com.techbytedev.signboardmanager.dto.response.ArticleResponseDTO;
import com.techbytedev.signboardmanager.dto.response.CategoryWithArticlesDTO;
import com.techbytedev.signboardmanager.entity.ArticleCategory;
import com.techbytedev.signboardmanager.service.ArticleCategoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class ArticleCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCategoryController.class);

    @Autowired
    private ArticleCategoryService articleCategoryService;

    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        return name.toLowerCase()
                  .trim()
                  .replaceAll("[^a-z0-9\\s-]", "")
                  .replaceAll("\\s+", "-")
                  .replaceAll("-+", "-")
                  .replaceAll("^-|-$", "");
    }

    @GetMapping("/article-categories")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories', 'GET')")
    public ResponseEntity<Map<String, Object>> getAllArticleCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("Fetching all article categories for admin with pagination: page={}, size={}", page, size);

        Page<ArticleCategory> categoryPage = articleCategoryService.getAllArticleCategories(page - 1, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", categoryPage.getContent());
        response.put("pageNumber", categoryPage.getNumber() + 1);
        response.put("pageSize", categoryPage.getSize());
        response.put("totalPages", categoryPage.getTotalPages());
        response.put("totalElements", categoryPage.getTotalElements());
        response.put("last", categoryPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/article-categories/tree")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/tree', 'GET')")
    public ResponseEntity<List<ArticleCategoryResponseDTO>> getArticleCategoryTree() {
        logger.debug("Fetching article category tree");
        List<ArticleCategoryResponseDTO> tree = articleCategoryService.getArticleCategoryTree();
        return ResponseEntity.ok(tree);
    }

   

    @PostMapping("/article-categories")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories', 'POST')")
    public ResponseEntity<ArticleCategory> createArticleCategory(@RequestBody Map<String, Object> requestData) {
        logger.debug("Creating new article category with data: {}", requestData);

        try {
            // Convert request data to ArticleCategory
            ArticleCategory category = new ArticleCategory();
            category.setName((String) requestData.get("name"));
            category.setSlug(generateSlug((String) requestData.get("name")));
            category.setDescription((String) requestData.get("description"));
            category.setActive(true);
            category.setLevel(0); // Set default level
            category.setSortOrder(0); // Set default sort order

            // Handle parentId
            if (requestData.get("parentId") != null && !(requestData.get("parentId") instanceof String && ((String) requestData.get("parentId")).trim().isEmpty())) {
                try {
                    Integer parentId = Integer.valueOf(requestData.get("parentId").toString());
                    if (parentId > 0) {
                        ArticleCategory parent = articleCategoryService.getArticleCategoryById(parentId);
                        if (parent == null) {
                            return ResponseEntity.badRequest().body(null);
                        }
                        category.setParentCategory(parent);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid parentId format: {}", requestData.get("parentId"));
                    return ResponseEntity.badRequest().body(null);
                }
            }

            ArticleCategory saved = articleCategoryService.createArticleCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating article category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error creating article category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/article-categories/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/{id}', 'PUT')")
    public ResponseEntity<ArticleCategory> updateArticleCategory(@PathVariable int id, @RequestBody ArticleCategory category) {
        logger.debug("Updating article category with id: {}", id);
        try {
            ArticleCategory updated = articleCategoryService.updateArticleCategory(id, category);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating article category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error updating article category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/article-categories/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/{id}', 'DELETE')")
    public ResponseEntity<String> deleteArticleCategory(@PathVariable int id) {
        logger.debug("Deleting article category with id: {}", id);
        try {
            articleCategoryService.deleteArticleCategory(id);
            return ResponseEntity.ok("Xóa danh mục bài viết thành công");
        } catch (RuntimeException e) {
            logger.error("Error deleting article category with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/article-categories/search")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/search', 'GET')")
    public ResponseEntity<List<ArticleCategory>> searchArticleCategories(@RequestParam String name) {
        logger.debug("Searching article categories with name containing: {}", name);
        return ResponseEntity.ok(articleCategoryService.searchArticleCategories(name));
    }

    @GetMapping("/article-categories/getAllArticles/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/getAllArticles/{id}', 'GET')")
    public ResponseEntity<?> getAllArticlesByCategoryAndSubcategories(
            @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        logger.debug("Getting all articles for category and subcategories with id: {}, page: {}, size: {}, search: {}", id, page, size, search);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            CategoryWithArticlesDTO result = articleCategoryService.getAllArticlesByCategoryAndSubcategories(id, pageable, search);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error getting articles for category {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/article-categories/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article-categories/{id}', 'GET')")
    public ResponseEntity<ArticleCategory> getArticleCategoryById(@PathVariable int id) {
        logger.debug("Fetching article category with id: {}", id);
        ArticleCategory category = articleCategoryService.getArticleCategoryById(id);
        if (category == null) {
            logger.warn("Article category with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(category);
    }
}
