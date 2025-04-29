package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.DesignService;

import org.springframework.core.env.Environment; // Không cần thiết nếu chỉ lấy key qua @Value hoặc đã inject chỗ khác
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Đảm bảo Spring Security cấu hình đúng để inject User
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/design")
public class DesignController {

    private static final Logger logger = LoggerFactory.getLogger(DesignController.class);
    private final DesignService designService;
    // private final Environment environment; // Bỏ nếu không dùng trực tiếp ở đây

    // Constructor
    public DesignController(DesignService designService /*, Environment environment */) {
        this.designService = designService;
        // this.environment = environment;
    }

    @GetMapping("/templates")
    public List<DesignTemplate> getTemplates(@RequestParam String category) {
        // Lấy danh sách template theo category
        return designService.getTemplates(category);
    }

    /**
     * Endpoint để tạo một bản ghi UserDesign ban đầu trong DB.
     * Frontend sẽ gọi API này TRƯỚC khi gọi API tạo thiết kế của Canva.
     * Nó trả về UserDesign đã được tạo với ID duy nhất.
     */
    @PostMapping("/create")
    public ResponseEntity<UserDesign> createDesign(@AuthenticationPrincipal User user, @RequestBody CreateDesignRequest request) {
        if (user == null) {
             logger.warn("Unauthorized attempt to create design.");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            logger.info("User {} creating initial design record with name: {}", user.getId(), request.designName());
            UserDesign createdDesign = designService.createDesign(user, request.designName(), request.designType());
            return ResponseEntity.ok(createdDesign);
        } catch (Exception e) {
            logger.error("Error creating initial design record for user {}", user.getId(), e);
            // Trả về lỗi cụ thể hơn nếu có thể
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint để cập nhật thông tin từ Canva (canvaDesignId, canvaEditUrl)
     * vào một UserDesign đã tồn tại (với ID là {id}).
     * Frontend gọi API này SAU KHI tạo thiết kế trên Canva thành công.
     */
    @PostMapping("/save/{id}") // Hoặc dùng @PutMapping nếu đúng ngữ nghĩa REST hơn
    public ResponseEntity<UserDesign> updateDesignWithCanvaInfo(@PathVariable Integer id,
                                            @RequestBody UpdateCanvaInfoRequest request,
                                            @AuthenticationPrincipal User user) {
         if (user == null) {
             logger.warn("Unauthorized attempt to save Canva info for design {}.", id);
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         try {
             logger.info("User {} saving Canva info for design {}: canvaDesignId={}", user.getId(), id, request.canvaDesignId());
             // Cần kiểm tra quyền sở hữu thiết kế trong service
             UserDesign updatedDesign = designService.saveCanvaDesignInfo(id, user.getId(), request.canvaDesignId(), request.canvaEditUrl());
             if (updatedDesign == null) {
                  // Có thể do không tìm thấy design id hoặc không có quyền
                  logger.warn("User {} failed to save Canva info for design {}. Not found or unauthorized.", user.getId(), id);
                  return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Hoặc FORBIDDEN
             }
             return ResponseEntity.ok(updatedDesign);
         } catch (Exception e) {
              logger.error("Error saving Canva info for design {} for user {}", id, user.getId(), e);
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }
    }


    /**
     * Endpoint để người dùng gửi thiết kế (thay đổi trạng thái).
     */
    @PostMapping("/submit/{id}")
    public ResponseEntity<UserDesign> submitDesign(@PathVariable Integer id, @AuthenticationPrincipal User user) {
         if (user == null) {
             logger.warn("Unauthorized attempt to submit design {}.", id);
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
        try {
            logger.info("User {} submitting design {}", user.getId(), id);
            // Cần kiểm tra quyền sở hữu và trạng thái hợp lệ trong service
            UserDesign submittedDesign = designService.submitDesign(id, user.getId());
             if (submittedDesign == null) {
                  logger.warn("User {} failed to submit design {}. Not found, unauthorized, or invalid state.", user.getId(), id);
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Hoặc NOT_FOUND/FORBIDDEN
             }
            return ResponseEntity.ok(submittedDesign);
        } catch (Exception e) {
            logger.error("Error submitting design {} for user {}", id, user.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-designs")
    public ResponseEntity<List<UserDesign>> getMyDesigns(@AuthenticationPrincipal User user) {
         if (user == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
        return ResponseEntity.ok(designService.getUserDesigns(user.getId()));
    }

    // Endpoint này có thể cần phân quyền chỉ cho admin
    @GetMapping("/submitted")
    public List<UserDesign> getSubmittedDesigns() {
        // Thêm kiểm tra quyền ADMIN ở đây nếu cần
        return designService.getSubmittedDesigns();
    }

    // Endpoint này không cần thiết nếu Client ID đã được inject vào CanvaAuthController
    // và frontend không cần lấy trực tiếp Client ID.
    // @GetMapping("/canva-api-key")
    // public ResponseEntity<String> getCanvaApiKey() {
    //     String canvaApiKey = environment.getProperty("canva.api.key");
    //     return ResponseEntity.ok(canvaApiKey);
    // }
}

// Records cho Request Bodies
record CreateDesignRequest(String designName, String designType) {}
record UpdateCanvaInfoRequest(String canvaDesignId, String canvaEditUrl) {} // Đổi tên cho rõ nghĩa