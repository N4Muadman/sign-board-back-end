package com.techbytedev.signboardmanager.controller;


import com.techbytedev.signboardmanager.dto.response.ProductDTO;
import com.techbytedev.signboardmanager.dto.response.ProductImageDTO;
import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.service.CategoryService;
import com.techbytedev.signboardmanager.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/parent")
    public List<Category> getParentCategories() {
        return categoryService.getParentCategories();
    }

    @GetMapping("/child/{parentId}")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable("parentId") int parentId) {
        List<Category> childCategories = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(childCategories);
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Map<String, Object>> getProductsByCategoryId(
            @PathVariable int categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Kiểm tra danh mục tồn tại
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Danh mục không tồn tại");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // Lấy sản phẩm với phân trang
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Product> productPage = productService.getProductsByCategoryAndSubcategories(categoryId, pageable);

            // Ánh xạ Product sang ProductDTO
            List<ProductDTO> productDTOs = productPage.getContent().stream()
                    .map(this::convertToProductDTO)
                    .collect(Collectors.toList());

            // Tạo Page<ProductDTO>
            Page<ProductDTO> productDTOPage = new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("content", productDTOPage.getContent());
            response.put("pageNumber", productDTOPage.getNumber() + 1);
            response.put("pageSize", productDTOPage.getSize());
            response.put("totalPages", productDTOPage.getTotalPages());
            response.put("totalElements", productDTOPage.getTotalElements());
            response.put("last", productDTOPage.isLast());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Phương thức ánh xạ Product sang ProductDTO
    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setDimensions(product.getDimensions());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setSku(product.getSku());
        dto.setFeatured(product.isFeatured());
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setDeletedAt(product.getDeletedAt());

        // Ánh xạ danh sách ProductImage
        if (product.getImages() != null) {
            List<ProductImageDTO> imageDTOs = product.getImages().stream()
                    .map(image -> {
                        ProductImageDTO imageDTO = new ProductImageDTO();
                        imageDTO.setId(image.getId());
                        imageDTO.setImageUrl(image.getImageUrl());
                        return imageDTO;
                    })
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }

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
}