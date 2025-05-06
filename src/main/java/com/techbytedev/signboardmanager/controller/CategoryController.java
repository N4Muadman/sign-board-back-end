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

    //CUSTOMER
    // hiển thị danh mục cha
    @GetMapping("/parent")
    public List<Category> getParentCategories() {
        return categoryService.getParentCategories();
    }
    // hiển thị danh mục con theo danh mục cha
    @GetMapping("/child/{parentId}")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable("parentId") int parentId) {
        List<Category> childCategories = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(childCategories);
    }
    //ADMIN
    // lấy danh sách danh mục
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
  
    // tìm kiếm danh mục
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategory(@RequestParam String name) {
        List<Category> categories = categoryService.searchCategory(name);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
