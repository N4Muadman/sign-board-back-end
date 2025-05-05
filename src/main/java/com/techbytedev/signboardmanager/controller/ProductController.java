package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //ADMIN
    // lấy danh sách sản phẩm
    @GetMapping("/list")
    public ResponseEntity<List<Product>> getList() {
        return ResponseEntity.ok(productService.findAll());
    }

    

    //CUSTOMER
    // hiển thị chi tiết sản phẩm
    @GetMapping("/{id}")
        public ProductResponse getProductDetails(@PathVariable int id) {
            return productService.getProductDetailsById(id);
    }
    // lọc sản phẩm
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
