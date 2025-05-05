package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.MaterialResponse;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.Material;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.entity.ProductImage;
import com.techbytedev.signboardmanager.entity.ProductMaterial;
import com.techbytedev.signboardmanager.repository.ProductImageRepository;
import com.techbytedev.signboardmanager.repository.ProductMaterialRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductMaterialRepository productMaterialRepository;

    // lấy danh sách sản phẩm
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // lấy danh sách sản phẩm theo danh mục
    public List<ProductResponse> getProductsWithPrimaryImageByCategoryId(int categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return convertToProductResponseList(products);
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
        existingProduct.setDiscountPercent(product.getDiscountPercent());
        existingProduct.setDiscountedPrice(product.getDiscountedPrice());
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
    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return convertToProductResponseList(products);
    }

    public List<ProductResponse> convertToProductResponseList(List<Product> products) {
        List<ProductResponse> result = new ArrayList<>();

        for (Product product : products) {
            ProductImage primaryImage = productImageRepository.findFirstByProductIdAndIsPrimaryTrue(product.getId());
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setPrice(product.getPrice());
            productResponse.setDiscount(product.getDiscountPercent());
            productResponse.setDiscountPrice(product.getDiscountedPrice());
            if (primaryImage != null) {
                productResponse.setImageURL("/images/" + primaryImage.getImageUrl());
            }
            result.add(productResponse);
        }
        return result;
    }
    // hiển thị sản phẩm chi tiết theo id sản phẩm
    public ProductResponse getProductDetailsById(int productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (!productOpt.isPresent()) {
            return null;
        }
        Product product = productOpt.get();
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setDiscount(product.getDiscountPercent());
        productResponse.setDiscountPrice(product.getDiscountedPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setDimensions(product.getDimensions());

        ProductImage primaryImage = productImages.stream()
                .filter(image -> image.isPrimary())
                .findFirst()
                .orElse(null);
        if (primaryImage != null) {
            productResponse.setImageURL("/images/" + primaryImage.getImageUrl());
        }

        for (ProductImage productImage : productImages) {
            productResponse.getImageURLs().add("/images/" + productImage.getImageUrl());
        }
        // Lấy vật liệu của sản phẩm từ bảng product_materials
        List<ProductMaterial> productMaterials = productMaterialRepository.findByProductId(productId);
        List<MaterialResponse> materialResponses = new ArrayList<>();

        for (ProductMaterial productMaterial : productMaterials) {
            Material material = productMaterial.getMaterial();
            MaterialResponse materialResponse = new MaterialResponse();
            materialResponse.setId(material.getId());
            materialResponse.setName(material.getName());
            materialResponse.setDescription(material.getDescription());
            materialResponses.add(materialResponse);
        }

        productResponse.setMaterials(materialResponses);
        return productResponse;
    }
    // lọc sản phẩm theo giá sau khi đã giảm
    public List<Product> filterProductsByDiscountedPrice(double minPrice, double maxPrice) {
        return productRepository.findProductsByDiscountedPriceBetween(minPrice, maxPrice);
    }
    // Lấy danh sách sản phẩm theo giá giảm dần
    public List<Product> getProductsSortedByDiscountedPriceDesc() {
        return productRepository.findAllByOrderByDiscountedPriceDesc();
    }

    // Lấy danh sách sản phẩm theo giá tăng dần
    public List<Product> getProductsSortedByDiscountedPriceAsc() {
        return productRepository.findAllByOrderByDiscountedPriceAsc();
    }
}
