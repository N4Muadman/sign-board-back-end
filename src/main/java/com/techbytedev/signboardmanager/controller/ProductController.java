package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //CUSTOMER
    // hiển thị danh sách sản phẩm thuộc danh mục con
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable int categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<ProductResponse> productPage = productService.getProductsByCategoryId(categoryId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("pageNumber", productPage.getPageable().getPageNumber() + 1);
        response.put("pageSize", productPage.getSize());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
    // hiển thị chi tiết sản phẩm
    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/{id}', 'GET')")
    public ProductResponse getProductDetails(@PathVariable int id) {
        return productService.getProductDetailsById(id);
    }

    // CUSTOMER: Lọc sản phẩm
    @GetMapping("/filter")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/filter', 'GET')")
    public List<ProductResponse> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "none") String sort
    ) {
        return productService.filterProducts(name, categoryId, minPrice, maxPrice, sort);
    }
}