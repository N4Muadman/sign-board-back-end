package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.service.CanvaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/design")
public class DesignController {

    private final CanvaService canvaService;

    @Autowired
    public DesignController(CanvaService canvaService) {
        this.canvaService = canvaService;
    }

    @GetMapping("/start")
    public ResponseEntity<String> startDesign(@AuthenticationPrincipal OAuth2User principal) {
        Long userId = Long.valueOf(principal.getAttribute("sub"));
        String token = principal.getAttribute("access_token");
        try {
            String url = canvaService.startDesign(userId, token);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/return")
    public ResponseEntity<String> handleReturn(@RequestParam String designId, @AuthenticationPrincipal OAuth2User principal) {
        String token = principal.getAttribute("access_token");
        try {
            String path = canvaService.exportDesign(designId, token);
            return ResponseEntity.ok("Saved at: " + path);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
}