package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public long countByCategoryId(int categoryId);
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    // Trong ProductRepository.java
Page<Product> findByCategoryId(int categoryId, Pageable pageable);
Page<Product> findByCategoryIdIn(List<Integer> categoryIds, Pageable pageable);
    
}
