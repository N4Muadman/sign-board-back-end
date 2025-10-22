package com.techbytedev.signboardmanager.service;


import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techbytedev.signboardmanager.dto.response.ArticleCategoryResponseDTO;
import com.techbytedev.signboardmanager.dto.response.ArticleResponseDTO;
import com.techbytedev.signboardmanager.dto.response.CategoryWithArticlesDTO;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.ArticleCategory;
import com.techbytedev.signboardmanager.repository.ArticleCategoryRepository;
import com.techbytedev.signboardmanager.repository.ArticleRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ArticleCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCategoryService.class);

    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public ArticleCategory createArticleCategory(ArticleCategory category) {
        if (category.getParentCategory() != null) {
            ArticleCategory parent = articleCategoryRepository.findById(category.getParentCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));

            // Validate maximum depth before creating
            int newLevel = parent.getLevel() + 1;
            if (newLevel > 1) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 2 levels are allowed (0-1).");
            }

            category.setParentCategory(parent);
        }
        return articleCategoryRepository.save(category);
    }

    public ArticleCategory updateArticleCategory(int id, ArticleCategory categoryDetails) {
        ArticleCategory category = articleCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article category not found"));

        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setDescription(categoryDetails.getDescription());
        category.setActive(categoryDetails.isActive());

        if (categoryDetails.getParentCategory() != null) {
            ArticleCategory parent = articleCategoryRepository.findById(categoryDetails.getParentCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));

            // Validate maximum depth before updating
            int newLevel = parent.getLevel() + 1;
            if (newLevel > 1) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 2 levels are allowed (0-1).");
            }

            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        return articleCategoryRepository.save(category);
    }

    public void deleteArticleCategory(int id) {
        ArticleCategory category = articleCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article category not found"));

        // Check if category has articles
        List<Article> articles = articleRepository.findByCategoryId(id);
        if (!articles.isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing articles");
        }

        articleCategoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Page<ArticleCategory> getAllArticleCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleCategoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ArticleCategoryResponseDTO> getArticleCategoryTree() {
        // Lấy tất cả categories để xây dựng tree
        List<ArticleCategory> allCategories = articleCategoryRepository.findByIsActiveTrue();

        // Xây dựng tree structure
        return buildCategoryTree(allCategories);
    }

    private List<ArticleCategoryResponseDTO> buildCategoryTree(List<ArticleCategory> categories) {
        // Tạo map để tìm parent nhanh
        Map<Integer, List<ArticleCategory>> childrenMap = new HashMap<>();

        // Nhóm các category theo parentId
        for (ArticleCategory category : categories) {
            Integer parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }

        // Lấy root categories (không có parent)
        List<ArticleCategory> rootCategories = childrenMap.getOrDefault(null, new ArrayList<>());

        // Sắp xếp theo sortOrder
        rootCategories.sort(Comparator.comparing(ArticleCategory::getSortOrder));

        // Convert root categories và đệ quy set children
        List<ArticleCategoryResponseDTO> result = new ArrayList<>();
        for (ArticleCategory category : rootCategories) {
            result.add(convertToDTOWithChildren(category, childrenMap));
        }
        return result;
    }

    private ArticleCategoryResponseDTO convertToDTOWithChildren(ArticleCategory category, Map<Integer, List<ArticleCategory>> childrenMap) {
        ArticleCategoryResponseDTO dto = new ArticleCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setActive(category.isActive());
        dto.setLevel(category.getLevel());
        dto.setSortOrder(category.getSortOrder());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        // Set parent info
        if (category.getParentCategory() != null) {
            dto.setParentId(category.getParentCategory().getId());
            dto.setParentName(category.getParentCategory().getName());
        }

        // Count articles in this category
        List<Article> articles = articleRepository.findByCategoryId(category.getId());
        dto.setArticleCount(articles.size());

        // Count children
        List<ArticleCategory> children = childrenMap.getOrDefault(category.getId(), new ArrayList<>());
        dto.setChildrenCount(children.size());

        // Tính tổng bài viết bao gồm cả bài viết của category này và tất cả children (đệ quy)
        int totalArticles = articles.size();
        for (ArticleCategory child : children) {
            totalArticles += calculateTotalChildrenArticlesWithSelf(child.getId(), childrenMap);
        }
        dto.setTotalChildrenArticlesCount(totalArticles);

        // Sắp xếp children theo sortOrder
        children.sort(Comparator.comparing(ArticleCategory::getSortOrder));

        // Đệ quy convert children
        List<ArticleCategoryResponseDTO> childrenDTOs = new ArrayList<>();
        for (ArticleCategory child : children) {
            childrenDTOs.add(convertToDTOWithChildren(child, childrenMap));
        }

        dto.setChildren(childrenDTOs);

        return dto;
    }

    private int calculateTotalChildrenArticlesWithSelf(int categoryId, Map<Integer, List<ArticleCategory>> childrenMap) {
        // Lấy category để tìm bài viết của chính nó
        ArticleCategory category = articleCategoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return 0;
        }

        int totalArticles = articleRepository.findByCategoryId(categoryId).size();

        // Đệ quy tính bài viết của tất cả children
        List<ArticleCategory> children = childrenMap.getOrDefault(categoryId, new ArrayList<>());
        for (ArticleCategory child : children) {
            totalArticles += calculateTotalChildrenArticlesWithSelf(child.getId(), childrenMap);
        }

        return totalArticles;
    }

    @Transactional(readOnly = true)
    public ArticleCategory getArticleCategoryById(int id) {
        Optional<ArticleCategory> category = articleCategoryRepository.findById(id);
        return category.orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ArticleCategory> searchArticleCategories(String name) {
        return articleCategoryRepository.findByIsActiveTrue().stream()
                .filter(category -> category.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryWithArticlesDTO getAllArticlesByCategoryAndSubcategories(int categoryId, Pageable pageable, String search) {
        // Check if category exists
        ArticleCategory rootCategory = articleCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // Get all categories to build hierarchy
        List<ArticleCategory> allCategories = articleCategoryRepository.findAll();
        Map<Integer, List<ArticleCategory>> childrenMap = buildChildrenMap(allCategories);

        // Get all category IDs including subcategories
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        findChildCategories(categoryId, allCategories, categoryIds);

        // Get paginated articles from these categories, with search filter
        Page<Article> articlePage;
        if (search != null && !search.trim().isEmpty()) {
            articlePage = articleRepository.findByCategoryIdInAndSearch((Collection<Integer>) categoryIds, search, pageable);
        } else {
            articlePage = articleRepository.findByCategoryIdIn((Collection<Integer>) categoryIds, pageable);
        }

        // Group articles by categoryId
        Map<Integer, List<Article>> articlesByCategory = new HashMap<>();
        for (Article article : articlePage.getContent()) {
            articlesByCategory.computeIfAbsent(article.getCategory().getId(), k -> new ArrayList<>()).add(article);
        }

        // Build nested DTO starting from root category
        CategoryWithArticlesDTO result = buildCategoryWithArticlesDTO(rootCategory, childrenMap, articlesByCategory, allCategories);
        logger.info("Built CategoryWithArticlesDTO: {}", result);
        return result;
    }

    private Map<Integer, List<ArticleCategory>> buildChildrenMap(List<ArticleCategory> categories) {
        Map<Integer, List<ArticleCategory>> childrenMap = new HashMap<>();
        for (ArticleCategory category : categories) {
            Integer parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }
        return childrenMap;
    }

    private CategoryWithArticlesDTO buildCategoryWithArticlesDTO(ArticleCategory category, Map<Integer, List<ArticleCategory>> childrenMap, Map<Integer, List<Article>> articlesByCategory, List<ArticleCategory> allCategories) {
        CategoryWithArticlesDTO dto = CategoryWithArticlesDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .isActive(category.isActive())
                .level(category.getLevel())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();

        // Set parent info
        if (category.getParentCategory() != null) {
            dto.setParentId(category.getParentCategory().getId());
            dto.setParentName(category.getParentCategory().getName());
        }

        // Set articles for this category
        List<Article> articles = articlesByCategory.getOrDefault(category.getId(), new ArrayList<>());
        List<ArticleResponseDTO> articleDTOs = articles.stream()
                .map(article -> ArticleResponseDTO.builder()
                        .id(article.getId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .excerpt(article.getExcerpt())
                        .slug(article.getSlug())
                        .imageBase64(article.getImageBase64())
                        .categoryId(article.getCategory().getId())
                        .isActive(article.isFeatured())  // Using isFeatured as isActive, assuming it's similar
                        .createdAt(article.getCreatedAt() != null ? java.sql.Timestamp.valueOf(article.getCreatedAt()) : null)
                        .updatedAt(article.getUpdatedAt() != null ? java.sql.Timestamp.valueOf(article.getUpdatedAt()) : null)
                        .build())
                .toList();
        dto.setArticles(articleDTOs);
        dto.setArticleCount(articleDTOs.size());

        // Set children
        List<ArticleCategory> children = childrenMap.getOrDefault(category.getId(), new ArrayList<>());
        children.sort(Comparator.comparing(ArticleCategory::getSortOrder));
        List<CategoryWithArticlesDTO> childrenDTOs = children.stream()
                .map(child -> buildCategoryWithArticlesDTO(child, childrenMap, articlesByCategory, allCategories))
                .toList();
        dto.setSubcategories(childrenDTOs);
        dto.setChildrenCount(childrenDTOs.size());

        // Calculate total children articles
        int totalArticles = articleDTOs.size();
        for (CategoryWithArticlesDTO child : childrenDTOs) {
            totalArticles += child.getTotalChildrenArticlesCount();
        }
        dto.setTotalChildrenArticlesCount(totalArticles);

        return dto;
    }

    private void findChildCategories(int parentId, List<ArticleCategory> allCategories, List<Integer> result) {
        for (ArticleCategory category : allCategories) {
            if (category.getParentCategory() != null && category.getParentCategory().getId() == parentId) {
                result.add(category.getId());
                findChildCategories(category.getId(), allCategories, result);
            }
        }
    }
}
