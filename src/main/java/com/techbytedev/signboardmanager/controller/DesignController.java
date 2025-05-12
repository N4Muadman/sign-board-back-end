package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.DesignService;
import com.techbytedev.signboardmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/design")
public class DesignController {

    private static final Logger logger = LoggerFactory.getLogger(DesignController.class);

    private final DesignService designService;
    private final UserService userService;

    @Autowired
    public DesignController(DesignService designService, UserService userService) {
        this.designService = designService;
        this.userService = userService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitDesign(
            @RequestParam(value = "canvaLink", required = false) String canvaLink,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication) {
        logger.info("Received submit request with canvaLink: {}, image: {}, notes: {}", canvaLink, image != null, notes);

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        Integer userId = extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body("User ID not found.");
        }

        try {
            // Validate that at least one design source is provided
            if ((canvaLink == null || canvaLink.isEmpty()) && (image == null || image.isEmpty())) {
                return ResponseEntity.badRequest().body("Please provide either a Canva link or an image.");
            }

            // Only one of canvaLink or image should be provided
            if (canvaLink != null && !canvaLink.isEmpty() && image != null && !image.isEmpty()) {
                return ResponseEntity.badRequest().body("Please provide either a Canva link or an image, not both.");
            }

            String designSource = null;
            if (canvaLink != null && !canvaLink.isEmpty()) {
                designSource = canvaLink;
            } else if (image != null && !image.isEmpty()) {
                designSource = designService.saveImageDesign(userId, image);
            }

            UserDesign userDesign = new UserDesign();
            userDesign.setDesignSource(designSource);
            userDesign.setNotes(notes);
            userDesign.setStatus(UserDesign.Status.SUBMITTED);
            userDesign.setCreatedAt(LocalDateTime.now());
            userDesign.setUpdatedAt(LocalDateTime.now());
            userDesign.setSubmittedAt(LocalDateTime.now());

            UserDesign savedDesign = designService.saveDesignSubmission(userId, userDesign);
            logger.info("Design submitted successfully with ID: {}", savedDesign.getId());
            return ResponseEntity.ok("Design submitted with ID: " + savedDesign.getId());
        } catch (IOException e) {
            logger.error("Error processing image: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error submitting design: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/user-designs")
    public ResponseEntity<List<UserDesign>> getUserDesigns(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(null);
        }

        Integer userId = extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        List<UserDesign> designs = designService.getUserDesigns(userId);
        return ResponseEntity.ok(designs);
    }

    @GetMapping("/submitted-designs")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<UserDesign>> getSubmittedDesigns() {
        List<UserDesign> submittedDesigns = designService.getSubmittedDesigns();
        return ResponseEntity.ok(submittedDesigns);
    }

    private Integer extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
            return Integer.valueOf(((org.springframework.security.oauth2.core.oidc.user.OidcUser) principal).getAttribute("sub"));
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            User user = userService.findByUsername(username);
            return user != null ? user.getId() : null;
        }
        return null;
    }
}