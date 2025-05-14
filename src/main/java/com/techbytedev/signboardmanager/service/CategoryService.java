package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.repository.CategoryRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElse(null);
    }

    // Lấy tất cả ID danh mục, bao gồm danh mục con đệ quy
    public List<Integer> getCategoryAndSubcategoryIds(int categoryId) {
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        collectSubcategoryIds(categoryId, categoryIds);
        return categoryIds;
    }

    private void collectSubcategoryIds(int parentId, List<Integer> categoryIds) {
        List<Category> subcategories = categoryRepository.findByParentCategoryId(parentId);
        for (Category subcategory : subcategories) {
            categoryIds.add(subcategory.getId());
            collectSubcategoryIds(subcategory.getId(), categoryIds);
        }
    }

    @Transactional
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public List<Category> getParentCategories() {
        List<Category> categories = categoryRepository.findByParentCategoryIdIsNull();
        for (Category category : categories) {
            if (category.getImageURL() != null) {
                category.setImageURL("/images/" + category.getImageURL());
            }
            for (Category childCategory : category.getChildCategories()) {
                if(childCategory.getImageURL() != null) {
                    childCategory.setImageURL("/images/" + childCategory.getImageURL());
                }
            }
        }
        return categories;
    }

    public List<Category> getChildCategories(int parentCategoryId) {
        Category parentCategory = getCategoryById(parentCategoryId);
        return parentCategory != null ? parentCategory.getChildCategories() : new ArrayList<>();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(int categoryId, Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setSlug(updatedCategory.getSlug());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setImageURL(updatedCategory.getImageURL());
        existingCategory.setParentCategory(updatedCategory.getParentCategory());
        existingCategory.setSortOrder(updatedCategory.getSortOrder());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(int categoryId) {
        if (categoryRepository.countByParentCategoryId(categoryId) > 0) {
            throw new IllegalStateException("Không thể xóa danh mục vì nó có danh mục con.");
        }
        if (productRepository.countByCategoryId(categoryId) > 0) {
            throw new IllegalStateException("Không thể xóa danh mục vì nó đang có sản phẩm.");
        }
        categoryRepository.deleteById(categoryId);
    }

    public List<Category> searchCategory(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }
}