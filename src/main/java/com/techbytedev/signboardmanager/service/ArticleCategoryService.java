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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
            if (newLevel > 2) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 3 levels are allowed (0-2).");
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
            if (newLevel > 2) {
                throw new IllegalArgumentException("Maximum category depth exceeded. Only 3 levels are allowed (0-2).");
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
        List<ArticleCategory> rootCategories = articleCategoryRepository.findRootCategoriesOrderedBySortOrder();

        return rootCategories.stream()
                .map(this::convertToDTO)
                .toList();
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

    private ArticleCategoryResponseDTO convertToDTO(ArticleCategory category) {
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
        List<ArticleCategory> children = articleCategoryRepository.findByParentCategoryIdAndIsActiveTrue(category.getId());
        dto.setChildrenCount(children.size());

        // Recursively add children
        if (!children.isEmpty()) {
            List<ArticleCategoryResponseDTO> childrenDTOs = children.stream()
                    .map(this::convertToDTO)
                    .toList();
            dto.setChildren(childrenDTOs);
        }

        return dto;
    }
}
