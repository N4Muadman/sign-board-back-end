package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(int categoryId);
    public long countByCategoryId(int categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
    @Query("SELECT p FROM Product p WHERE p.discountedPrice BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findProductsByDiscountedPriceBetween(
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice
    );
    List<Product> findAllByOrderByDiscountedPriceAsc();
    List<Product> findAllByOrderByDiscountedPriceDesc();
}
