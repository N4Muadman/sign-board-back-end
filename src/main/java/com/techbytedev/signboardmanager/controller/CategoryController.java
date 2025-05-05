package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
    @GetMapping("list")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
  
    // tìm kiếm danh mục
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategory(@RequestParam String name) {
        List<Category> categories = categoryService.searchCategory(name);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
