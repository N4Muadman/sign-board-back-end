package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CUSTOMER: Hiển thị chi tiết sản phẩm
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