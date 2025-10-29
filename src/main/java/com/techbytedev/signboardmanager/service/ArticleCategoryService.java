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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

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
            logger.info("Parent category set: {}", parent.getName());
        }
        return articleCategoryRepository.save(category);
    }

    public ArticleCategory updateArticleCategory(int id, ArticleCategory categoryDetails) {
        ArticleCategory category = articleCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article category not found"));

        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setImage64(categoryDetails.getImage64());
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

    public String getFirstArticleImage64(int id) {
        Article article = articleRepository.getFirstArticleByCategoryId(id);
        if (article == null) {
            return null;
        }
        return article.getImageBase64();
    }


    public Article getFirstArticle(int id) {
        return articleRepository.getFirstArticleByCategoryId(id);
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
        // 1. Lấy danh mục gốc
        ArticleCategory rootCategory = articleCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // 2. Lấy tất cả ID danh mục con (bao gồm cả danh mục gốc)
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        findChildCategoryIds(categoryId, categoryIds);

        // 3. Lấy tất cả danh mục liên quan trong một lần gọi
        List<ArticleCategory> allCategories = articleCategoryRepository.findAllById(categoryIds);
        // 4. Lấy bài viết đã phân trang
        Page<Article> articlePage;
        if (StringUtils.hasText(search)) {
            articlePage = articleRepository.findByCategoryIdInAndSearch(categoryIds, search, pageable);
        } else {
            articlePage = articleRepository.findByCategoryIdInWithCategory(categoryIds, pageable);
        }

        // 5. Nhóm bài viết theo categoryId
        Map<Integer, List<Article>> articlesByCategory = articlePage.getContent().stream()
                .collect(Collectors.groupingBy(article -> article.getCategory().getId()));

        // 6. Xây dựng cây danh mục
        Map<Integer, List<ArticleCategory>> childrenMap = buildChildrenMap(allCategories);

        // 7. Xây dựng kết quả
        CategoryWithArticlesDTO result = buildCategoryWithArticlesDTO(rootCategory, childrenMap, articlesByCategory, allCategories);
        logger.debug("Built CategoryWithArticlesDTO for category: {}", categoryId);
        return result;
    }
    
    private void findChildCategoryIds(int parentId, List<Integer> result) {
        List<ArticleCategory> children = articleCategoryRepository.findByParentCategoryIdAndIsActiveTrue(parentId);
        for (ArticleCategory child : children) {
            result.add(child.getId());
            findChildCategoryIds(child.getId(), result);
        }
    }

    private Map<Integer, List<ArticleCategory>> buildChildrenMap(List<ArticleCategory> categories) {
        Map<Integer, List<ArticleCategory>> childrenMap = new HashMap<>();
        for (ArticleCategory category : categories) {
            if (category.getParentCategory() != null) {
                childrenMap.computeIfAbsent(category.getParentCategory().getId(), k -> new ArrayList<>()).add(category);
            }
        }
        // Sắp xếp các danh mục con theo sortOrder
        for (List<ArticleCategory> children : childrenMap.values()) {
            children.sort(Comparator.comparingInt(ArticleCategory::getSortOrder));
        }
        return childrenMap;
    }

    private CategoryWithArticlesDTO buildCategoryWithArticlesDTO(
            ArticleCategory category, 
            Map<Integer, List<ArticleCategory>> childrenMap,
            Map<Integer, List<Article>> articlesByCategory, 
            List<ArticleCategory> allCategories) {
            
        CategoryWithArticlesDTO dto = new CategoryWithArticlesDTO();
        // Giữ nguyên các trường hiện có
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setActive(category.isActive());
        dto.setLevel(category.getLevel());
        dto.setSortOrder(category.getSortOrder());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        // Thiết lập thông tin parent
        if (category.getParentCategory() != null) {
            dto.setParentId(category.getParentCategory().getId());
            dto.setParentName(category.getParentCategory().getName());
        }

        // Thiết lập bài viết cho danh mục hiện tại
        List<Article> articles = articlesByCategory.getOrDefault(category.getId(), new ArrayList<>());
        dto.setArticles(mapToArticleDTOs(articles));
        dto.setArticleCount(articles.size());

        // Xử lý danh mục con
        List<ArticleCategory> children = childrenMap.getOrDefault(category.getId(), new ArrayList<>());
        List<CategoryWithArticlesDTO> childrenDTOs = children.stream()
                .map(child -> buildCategoryWithArticlesDTO(child, childrenMap, articlesByCategory, allCategories))
                .collect(Collectors.toList());

        dto.setSubcategories(childrenDTOs);
        dto.setChildrenCount(childrenDTOs.size());

        // Tính tổng số bài viết của tất cả danh mục con
        int totalArticles = articles.size();
        for (CategoryWithArticlesDTO child : childrenDTOs) {
            totalArticles += child.getTotalChildrenArticlesCount();
        }
        dto.setTotalChildrenArticlesCount(totalArticles);

        return dto;
    }
    
    private List<ArticleResponseDTO> mapToArticleDTOs(List<Article> articles) {
        return articles.stream()
            .map(article -> ArticleResponseDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .excerpt(article.getExcerpt())
                .slug(article.getSlug())
                .imageBase64(article.getImageBase64())
                .categoryId(article.getCategory().getId())
                .isActive(article.isFeatured())
                .createdAt(article.getCreatedAt() != null ? java.sql.Timestamp.valueOf(article.getCreatedAt()) : null)
                .updatedAt(article.getUpdatedAt() != null ? java.sql.Timestamp.valueOf(article.getUpdatedAt()) : null)
                .build())
            .collect(Collectors.toList());
    }


    
}
