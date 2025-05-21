package com.techbytedev.signboardmanager.entity;

import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDiscount(product.getDiscountPercent());
        response.setDiscountPrice(product.getDiscountedPrice());
        response.setDescription(product.getDescription());
        response.setDimensions(product.getDimensions());

        ProductImage primaryImage = productImageRepository.findFirstByProductIdAndIsPrimaryTrue(product.getId());
        if (primaryImage != null) {
            response.setImageURL("/images/" + primaryImage.getImageUrl());
        }

        return response;
    }

    public List<ProductResponse> toProductResponseList(List<Product> products) {
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
}
