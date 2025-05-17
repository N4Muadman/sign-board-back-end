package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Optional<Wishlist> findByUser_Id(int userId);
    Optional<Wishlist> findBySessionId(String sessionId);
}
