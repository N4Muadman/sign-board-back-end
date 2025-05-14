package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.CategoryService;
import com.techbytedev.signboardmanager.service.ProductService;
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
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/parent")
    public List<Category> getParentCategories() {
        return categoryService.getParentCategories();
    }

    @GetMapping("/child/{parentId}")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable("parentId") int parentId) {
        List<Category> childCategories = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(childCategories);
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Map<String, Object>> getProductsByCategoryId(
            @PathVariable int categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Danh mục không tồn tại");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Product> productPage = productService.getProductsByCategoryAndSubcategories(categoryId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", productPage.getContent());
            response.put("pageNumber", productPage.getNumber() + 1);
            response.put("pageSize", productPage.getSize());
            response.put("totalPages", productPage.getTotalPages());
            response.put("totalElements", productPage.getTotalElements());
            response.put("last", productPage.isLast());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categoryPage = categoryService.getAllCategories(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", categoryPage.getContent());
        response.put("pageNumber", categoryPage.getNumber() + 1);
        response.put("pageSize", categoryPage.getSize());
        response.put("totalPages", categoryPage.getTotalPages());
        response.put("totalElements", categoryPage.getTotalElements());
        response.put("last", categoryPage.isLast());

        return ResponseEntity.ok(response);
    }
}