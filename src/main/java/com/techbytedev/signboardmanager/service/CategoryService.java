package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.repository.CategoryRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return categoryRepository.findById( id)
                .orElse(null);
    }

    // lấy danh sách danh mục
    @Transactional
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    // Lấy danh mục cha
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentCategoryIdIsNull();
    }

    // Lấy danh mục con theo danh mục cha
    public List<Category> getChildCategories(int parentCategoryId) {
        Category parentCategory = getCategoryById(parentCategoryId);
        return parentCategory.getChildCategories();
    }

    // thêm danh mục
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // sửa danh mục
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
    // xóa danh mục
    public void deleteCategory(int categoryId) {

        if (categoryRepository.countByParentCategoryId(categoryId) > 0) {
            throw new IllegalStateException("Không thể xóa danh mục vì nó có danh mục con.");
        }

        if (productRepository.countByCategoryId(categoryId) > 0) {
            throw new IllegalStateException("Không thể xóa danh mục vì nó đang có sản phẩm.");
        }
        categoryRepository.deleteById(categoryId);
    }
    // tìm kiếm theo tên danh mục
   public List<Category> searchCategory(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
   }
}
