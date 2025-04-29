package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    //ADMIN
    // lấy danh sách sản phẩm
    @GetMapping("/list")
    public ResponseEntity<List<Product>> getList() {
        return ResponseEntity.ok(productService.findAll());
    }
    
    //CUSTOMER
    // tìm kiếm sản phẩm theo tên khi người dùng nhập vào từ bất kỳ
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }
    // lấy danh sách sản phẩm theo danh mục con
    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getProductsByCategoryId(@PathVariable int categoryId) {
        return productService.getProductsWithPrimaryImageByCategoryId(categoryId);
    }
    // hiển thị chi tiết sản phẩm
    @GetMapping("/{id}")
        public ProductResponse getProductDetails(@PathVariable int id) {
            return productService.getProductDetailsById(id);
    }
    // lọc theo giá sau khi đã giảm
    @GetMapping("/filter-by-price")
    public List<Product> filterProductsByPrice(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        return productService.filterProductsByDiscountedPrice(minPrice, maxPrice);
    }
    // lấy sản phẩm sắp xếp theo giá giảm dần
    @GetMapping("/sorted/desc")
    public List<Product> getProductsSortedByDiscountedPriceDesc() {
        return productService.getProductsSortedByDiscountedPriceDesc();
    }

    //lấy sản phẩm sắp xếp theo giá tăng dần
    @GetMapping("/sorted/asc")
    public List<Product> getProductsSortedByDiscountedPriceAsc() {
        return productService.getProductsSortedByDiscountedPriceAsc();
    }

}
