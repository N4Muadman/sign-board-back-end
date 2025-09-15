package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.WishlistItemResponse;
import com.techbytedev.signboardmanager.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists/")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add")
    public ResponseEntity<String> addProductToWishlist(
            @RequestParam("productId") int productId,
            @RequestParam("identifier") String usernameOrSessionId) {

        try {
            wishlistService.addProductToWishlist(productId, usernameOrSessionId);
            return ResponseEntity.ok("Đã thêm sản phẩm vào danh sách yêu thích.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    // hiển thị danh sách yêu thích
    @GetMapping("/{identifier}")
    public ResponseEntity<List<WishlistItemResponse>> getWishlistItems(
            @PathVariable("identifier") String identifier) {

        try {
            List<WishlistItemResponse> wishlistItems = wishlistService.getWishlistItems(identifier);
            return ResponseEntity.ok(wishlistItems);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // xóa một sản phẩm trong danh sách yêu thích
    @DeleteMapping("/{identifier}/items/{productId}")
    public ResponseEntity<?> removeProductFromWishlist(
            @PathVariable("identifier") String identifier,
            @PathVariable("productId") int productId) {

        try {
            wishlistService.removeProductFromWishlist(productId, identifier);
            return ResponseEntity
                    .ok("Đã xóa sản phẩm khỏi danh sách yêu thích.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Lỗi: " + e.getMessage());
        }
    }
}
