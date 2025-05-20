package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.UserDesignResponseDTO;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.EmailService;
import com.techbytedev.signboardmanager.service.FileStorageService;
import com.techbytedev.signboardmanager.service.UserDesignService;
import com.techbytedev.signboardmanager.service.UserService;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-designs")
public class UserDesignController {

    private static final Logger logger = LoggerFactory.getLogger(UserDesignController.class);
    private final UserDesignService userDesignService;
    private final FileStorageService fileStorageService;
    private final com.techbytedev.signboardmanager.service.UserService userService;
private final EmailService emailService; // Add EmailService

    public UserDesignController(UserDesignService userDesignService, FileStorageService fileStorageService, 
                               UserService userService, EmailService emailService) {
        this.userDesignService = userDesignService;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.emailService = emailService; // Inject EmailService
        logger.info("UserDesignController initialized");
    }

   @PostMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/user-designs/**', 'POST')")
    public ResponseEntity<UserDesign> taoThietKeNguoiDung(
            @PathVariable("id") Long userId,
            @RequestParam(value = "designImage", required = false) MultipartFile designImage,
            @RequestParam(value = "designLink", required = false) String designLink,
            @RequestParam(value = "desc", required = false) String desc
            ) throws IOException {
        logger.debug("Creating user design for userId: {}", userId);

        UserDesign userDesign = new UserDesign();
        userDesign.setUserId(userId);
        userDesign.setDescription(desc);

        if (designImage != null && !designImage.isEmpty()) {
            userDesign.setDesignImage(fileStorageService.saveFile(designImage));
            logger.info("Saved design image for userId: {}", userId);
        }
        if (designLink != null) {
            userDesign.setDesignLink(designLink);
        }

        UserDesign savedDesign = userDesignService.tao(userDesign);

        // Lấy thông tin người dùng
        Map<String, String> userContactInfo = userService.getUserContactInfo(userId);
        String userFullName = userContactInfo.get("fullName");
        String userEmail = userContactInfo.get("email");
        String userPhoneNumber = userContactInfo.get("phoneNumber");

        // Tìm tất cả admin
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<UserResponse> adminUsers = userService.searchUsers(null, null, "Admin", true, pageable);
        List<String> adminEmails = adminUsers.getContent().stream()
                .map(UserResponse::getEmail)
                .filter(email -> email != null && !email.equals("N/A"))
                .collect(Collectors.toList());

        // Định dạng thời gian gửi
        String submissionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        // Gửi email đến từng admin với thông tin chi tiết
        for (String adminEmail : adminEmails) {
            String emailContent = String.format(
                "<h3>Thông báo: Thiết kế mới được gửi</h3>" +
                "<p><strong>ID thiết kế:</strong> %d</p>" +
                "<p><strong>Người gửi:</strong> %s</p>" +
                "<p><strong>Email:</strong> %s</p>" +
                "<p><strong>Số điện thoại:</strong> %s</p>" +
                "<p><strong>Thời gian gửi:</strong> %s</p>" +
                "<p><strong>Mô tả:</strong> %s</p>" +
                "<p><strong>Link thiết kế:</strong> %s</p>" +
                "<p>%s</p>",
                savedDesign.getId(),
                userFullName,
                userEmail,
                userPhoneNumber,
                submissionTime,
                desc != null ? desc : "Không có mô tả",
                designLink != null ? designLink : "Không có link",
                designImage != null && !designImage.isEmpty() ? "Ảnh thiết kế được đính kèm." : "Không có ảnh"
            );
            try {
                emailService.sendEmail(adminEmail, "Thông báo thiết kế mới", emailContent, designImage);
            } catch (MessagingException e) {
                logger.error("Lỗi gửi email đến {}: {}", adminEmail, e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDesign);
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
    public ResponseEntity<Page<UserDesignResponseDTO>> getAllUserDesigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("Fetching all user designs for admin with pagination: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDesign> designPage = userDesignService.layTatCa(pageable);

        Page<UserDesignResponseDTO> responsePage = designPage.map(userDesign -> {
            // Lấy thông tin người dùng từ userId
            User user = userService.findById(userDesign.getUserId() != null ? userDesign.getUserId().intValue() : null);
            return new UserDesignResponseDTO(
                    userDesign.getId(),
                    userDesign.getDesignImage(),
                    userDesign.getDesignLink(),
                    userDesign.getStatus(),
                    userDesign.getDescription(),
                    user != null ? user.getFullName() : "Unknown",
                    user != null ? user.getEmail() : "Unknown",
                    user != null ? user.getPhoneNumber() : "Unknown"
            );
        });

        return ResponseEntity.ok(responsePage);
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