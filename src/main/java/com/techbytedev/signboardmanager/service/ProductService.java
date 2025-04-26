package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // lấy danh sách sản phẩm
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // lấy danh sách sản phẩm theo danh mục
    public List<Product> getProductsByCategoryId(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // thêm sản phẩm
    public Product saveProduct (Product product) {
        return productRepository.save(product);
    }
    // sửa sản phẩm
    public Product updateProduct (int productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setActive(product.isActive());
        existingProduct.setDimensions(product.getDimensions());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setSku(product.getSku());
        existingProduct.setSlug(product.getSlug());
        return productRepository.save(existingProduct);
    }
    // xóa sản phẩm
    public void deleteProduct (int productId) {
        productRepository.deleteById(productId);
    }
    // Tìm kiếm sản phẩm theo tên
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
