package com.techbytedev.signboardmanager.controller;

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
    // thêm sản phẩm
    @PostMapping("/create")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product saveProduct = productService.saveProduct(product);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }
    // sửa sản phẩm
    @PutMapping("/edit/{id}")
    public Product edit(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }
    // xóa sản phẩm
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm");
        }
    }
    //CUSTOMER
    // tìm kiếm sản phẩm theo tên khi người dùng nhập vào từ bất kỳ
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    // lấy danh sách sản phẩm theo danh mục con
    @GetMapping("/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable int categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

}
