package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    ProductImage findFirstByProductIdAndIsPrimaryTrue(int productId);
    List<ProductImage> findByProductId(int productId);
}
