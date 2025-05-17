package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.entity.Wishlist;
import com.techbytedev.signboardmanager.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
    Optional<WishlistItem> findByWishlistAndProduct(Wishlist wishlist, Product product);
}
