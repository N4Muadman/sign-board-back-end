package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.ArticleCategoryResponseDTO;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.ArticleCategory;
import com.techbytedev.signboardmanager.repository.ArticleCategoryRepository;
import com.techbytedev.signboardmanager.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ArticleCategoryService {

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
}
