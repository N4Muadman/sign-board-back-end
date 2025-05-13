package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // CUSTOMER: Hiển thị chi tiết sản phẩm
    @GetMapping("/{id}")
    public ProductResponse getProductDetails(@PathVariable int id) {
        return productService.getProductDetailsById(id);
    }
  // lấy danh sách sản phẩm
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = productService.findAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("pageNumber", productPage.getNumber() + 1);
        response.put("pageSize", productPage.getSize());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());
        response.put("last", productPage.isLast());

        return ResponseEntity.ok(response);
    }
    // CUSTOMER: Lọc sản phẩm
    @GetMapping("/filter")
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