package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(int categoryId);
    public long countByCategoryId(int categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
}
