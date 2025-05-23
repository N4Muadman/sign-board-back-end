package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.AuthRequest;
import com.techbytedev.signboardmanager.dto.request.RegisterRequest;
import com.techbytedev.signboardmanager.dto.request.ResetPasswordRequest;
import com.techbytedev.signboardmanager.dto.response.AuthResponse;
import com.techbytedev.signboardmanager.service.AuthService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Verification code sent to email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping("/google-login")
    public ResponseEntity<String> googleLogin() {
        return ResponseEntity.ok("Redirecting to Google login...");
    }

    @GetMapping("/google-callback")
    public ResponseEntity<AuthResponse> googleCallback(OAuth2AuthenticationToken authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String fullName = oidcUser.getFullName();
        AuthResponse response = authService.googleLogin(email, fullName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/callback")
    public String handleOAuthCallback(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            // Lấy access token từ principal (phụ thuộc vào cách bạn cấu hình CustomOidcUserService)
            String accessToken = principal.getAttribute("access_token");
            if (accessToken != null) {
                // Chuyển hướng về frontend với token hoặc thông báo
                return "Đăng nhập thành công! Bạn sẽ được chuyển hướng. <script>window.location.href='http://127.0.0.1:3000/';</script>";
            }
        }
        return "Đăng nhập thất bại!";
    }
}