package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public long countByCategoryId(int categoryId);
}
