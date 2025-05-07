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
}
