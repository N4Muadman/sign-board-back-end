package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.FileStorageService;
import com.techbytedev.signboardmanager.service.UserDesignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user-designs")
public class UserDesignController {

    private static final Logger logger = LoggerFactory.getLogger(UserDesignController.class);
    private final UserDesignService userDesignService;
    private final FileStorageService fileStorageService;

    public UserDesignController(UserDesignService userDesignService, FileStorageService fileStorageService) {
        this.userDesignService = userDesignService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserDesign> taoThietKeNguoiDung(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "designImage", required = false) MultipartFile designImage,
            @RequestParam(value = "designLink", required = false) String designLink) throws IOException {

        UserDesign userDesign = new UserDesign();
        userDesign.setUserId(userId);

        if (designImage != null && !designImage.isEmpty()) {
            userDesign.setDesignImage(fileStorageService.saveFile(designImage));
        }
        else {
            logger.warn("Thiết kế không có hình ảnh");
        }
        if (designLink != null) {
            userDesign.setDesignLink(designLink);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userDesignService.tao(userDesign));
    }

    // Hỗ trợ cả "/api/user-designs" và "/api/user-designs/"
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<UserDesign>> layTatCaThietKeNguoiDung() {
        return ResponseEntity.ok(userDesignService.layTatCa());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDesign> layThietKeTheoId(@PathVariable("id") Long id) {
        UserDesign userDesign = userDesignService.layTheoId(id);
        return userDesign != null ? ResponseEntity.ok(userDesign) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/{userId}")
    public ResponseEntity<UserDesign> capNhatThietKeNguoiDung(
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId,
            @RequestParam(value = "designImage", required = false) MultipartFile designImage,
            @RequestParam(value = "designLink", required = false) String designLink) throws IOException {

        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        userDesign.setUserId(userId);

        if (designImage != null && !designImage.isEmpty()) {
            userDesign.setDesignImage(fileStorageService.saveFile(designImage));
        }
        if (designLink != null) {
            userDesign.setDesignLink(designLink);
        }

        return ResponseEntity.ok(userDesignService.capNhat(userDesign));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaThietKeNguoiDung(@PathVariable("id") Long id) {
        if (userDesignService.xoa(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{designId}/templates")
    public ResponseEntity<Void> ganMauThietKe(
            @PathVariable("designId") Long designId,
            @RequestBody List<Long> templateIds) {

        userDesignService.ganMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{designId}/templates")
    public ResponseEntity<Void> goMauThietKe(
            @PathVariable("designId") Long designId,
            @RequestBody List<Long> templateIds) {

        userDesignService.goMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }
}
