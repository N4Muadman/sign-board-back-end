package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.WishlistItemResponse;
import com.techbytedev.signboardmanager.entity.*;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import com.techbytedev.signboardmanager.repository.WishlistItemRepository;
import com.techbytedev.signboardmanager.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, WishlistItemRepository wishlistItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public void addProductToWishlist(int productId, String usernameOrSessionId) {
        Optional<User> userOpt = userRepository.findByUsername(usernameOrSessionId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
        Wishlist wishlist = null;

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            wishlist = wishlistRepository.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        Wishlist newWishlist = new Wishlist();
                        newWishlist.setUser(user);
                        return wishlistRepository.save(newWishlist);
                    });
        } else {
            wishlist = wishlistRepository.findBySessionId(usernameOrSessionId)
                    .orElseGet(() -> {
                        Wishlist newWishlist = new Wishlist();
                        newWishlist.setSessionId(usernameOrSessionId);
                        return wishlistRepository.save(newWishlist);
                    });
        }

        Optional<WishlistItem> existingItem = wishlistItemRepository.findByWishlistAndProduct(wishlist, product);

        if (existingItem.isEmpty()) {
            WishlistItem newItem = new WishlistItem();
            newItem.setWishlist(wishlist);
            newItem.setProduct(product);
            wishlistItemRepository.save(newItem);
        }
    }
    @Transactional
    public List<WishlistItemResponse> getWishlistItems(String identifier) {
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        Wishlist wishlist;

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            wishlist = wishlistRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy wishlist của người dùng"));
        } else {
            wishlist = wishlistRepository.findBySessionId(identifier)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy wishlist của khách"));
        }
        if (wishlist.getItems() == null || wishlist.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        List<WishlistItemResponse> responseList = new ArrayList<>();
        for(WishlistItem item : wishlist.getItems()) {
            Product p = item.getProduct();
            String imageUrl = p.getImages().stream()
                    .filter(image -> image.isPrimary())
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(null);
            responseList.add(new WishlistItemResponse(
                    p.getName(),
                    imageUrl != null ? "/images/" + imageUrl : null,
                    p.getDiscountedPrice()
            ));
        }
        return responseList;
    }
    public void removeProductFromWishlist(int productId, String identifier) {
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        Wishlist wishlist;

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            wishlist = wishlistRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy wishlist của người dùng"));
        } else {
            wishlist = wishlistRepository.findBySessionId(identifier)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy wishlist của khách"));
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Optional<WishlistItem> itemOpt = wishlistItemRepository.findByWishlistAndProduct(wishlist, product);

        if (itemOpt.isPresent()) {
            wishlistItemRepository.delete(itemOpt.get());
        }
    }

}
