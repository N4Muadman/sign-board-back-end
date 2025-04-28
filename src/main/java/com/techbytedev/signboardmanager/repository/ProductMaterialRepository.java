package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.ProductMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMaterialRepository extends JpaRepository<ProductMaterial, Integer> {
    List<ProductMaterial> findByProductId(int productId);
}
