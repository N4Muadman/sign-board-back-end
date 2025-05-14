package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.FileStorageService;
import com.techbytedev.signboardmanager.service.UserDesignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-designs")
public class UserDesignController {

    private static final Logger logger = LoggerFactory.getLogger(UserDesignController.class);
    private final UserDesignService userDesignService;
    private final FileStorageService fileStorageService;

    public UserDesignController(UserDesignService userDesignService, FileStorageService fileStorageService) {
        this.userDesignService = userDesignService;
        this.fileStorageService = fileStorageService;
        logger.info("UserDesignController initialized");
    }

    // Tạo thiết kế mới
    @PostMapping("/{userId}")
    public ResponseEntity<UserDesign> taoThietKeNguoiDung(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "designImage", required = false) MultipartFile designImage,
            @RequestParam(value = "designLink", required = false) String designLink,
            Authentication authentication) throws IOException {
        logger.debug("Creating user design for userId: {}", userId);
        // Kiểm tra quyền: Chỉ người dùng sở hữu mới được tạo
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userId, username)) {
            logger.warn("User {} not authorized to create design for userId {}", username, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDesign userDesign = new UserDesign();
        userDesign.setUserId(userId);

        if (designImage != null && !designImage.isEmpty()) {
            userDesign.setDesignImage(fileStorageService.saveFile(designImage));
            logger.info("Saved design image for userId: {}", userId);
        }
        if (designLink != null) {
            userDesign.setDesignLink(designLink);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userDesignService.tao(userDesign));
    }

    // Xử lý yêu cầu không hợp lệ (thiếu userId)
    @PostMapping
    public ResponseEntity<String> handleInvalidPost() {
        logger.warn("Invalid POST request to /api/user-designs without userId");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid request. Use /api/user-designs/{userId} with a valid userId.");
    }

    // Xem danh sách thiết kế của người dùng hiện tại
    @GetMapping("/my-designs")
    public ResponseEntity<List<UserDesign>> layThietKeCuaToi(Authentication authentication) {
        logger.debug("Fetching designs for authenticated user");
        String username = authentication.getName();
        Long userId = userDesignService.getUserIdByUsername(username);
        if (userId == null) {
            logger.warn("User {} not found", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<UserDesign> designs = userDesignService.layTatCa().stream()
                .filter(design -> design.getUserId().equals(userId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(designs);
    }

    // Xem chi tiết thiết kế của người dùng hiện tại
    @GetMapping("/{id}")
    public ResponseEntity<UserDesign> layThietKeTheoId(
            @PathVariable("id") Long id,
            Authentication authentication) {
        logger.debug("Fetching user design with id: {}", id);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Kiểm tra quyền: Chỉ người dùng sở hữu mới được xem
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userDesign.getUserId(), username)) {
            logger.warn("User {} not authorized to view design with id {}", username, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userDesign);
    }

    // Cập nhật thiết kế
    @PutMapping("/{id}/{userId}")
    public ResponseEntity<UserDesign> capNhatThietKeNguoiDung(
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId,
            @RequestParam(value = "designImage", required = false) MultipartFile designImage,
            @RequestParam(value = "designLink", required = false) String designLink,
            Authentication authentication) throws IOException {
        logger.debug("Updating user design with id: {} for userId: {}", id, userId);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Kiểm tra quyền
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userId, username)) {
            logger.warn("User {} not authorized to update design with id {}", username, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userDesign.setUserId(userId);
        if (designImage != null && !designImage.isEmpty()) {
            userDesign.setDesignImage(fileStorageService.saveFile(designImage));
            logger.info("Updated design image for id: {}", id);
        }
        if (designLink != null) {
            userDesign.setDesignLink(designLink);
        }

        return ResponseEntity.ok(userDesignService.capNhat(userDesign));
    }

    // Xóa thiết kế
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaThietKeNguoiDung(
            @PathVariable("id") Long id,
            Authentication authentication) {
        logger.debug("Deleting user design with id: {}", id);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Kiểm tra quyền
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userDesign.getUserId(), username)) {
            logger.warn("User {} not authorized to delete design with id {}", username, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userDesignService.xoa(id)) {
            return ResponseEntity.ok().build();
        }
        logger.warn("Failed to delete user design with id {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/{designId}/templates")
    public ResponseEntity<Void> ganMauThietKe(
            @PathVariable("designId") Long designId,
            @RequestBody List<Long> templateIds,
            Authentication authentication) {
        logger.debug("Assigning templates to designId: {}", designId);
        UserDesign userDesign = userDesignService.layTheoId(designId);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", designId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Kiểm tra quyền
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userDesign.getUserId(), username)) {
            logger.warn("User {} not authorized to assign templates for designId {}", username, designId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userDesignService.ganMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{designId}/templates")
    public ResponseEntity<Void> goMauThietKe(
            @PathVariable("designId") Long designId,
            @RequestBody List<Long> templateIds,
            Authentication authentication) {
        logger.debug("Removing templates from designId: {}", designId);
        UserDesign userDesign = userDesignService.layTheoId(designId);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", designId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Kiểm tra quyền
        String username = authentication.getName();
        if (!userDesignService.isUserAuthorized(userDesign.getUserId(), username)) {
            logger.warn("User {} not authorized to remove templates for designId {}", username, designId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userDesignService.goMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }
}