package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.UserRepository;
import com.techbytedev.signboardmanager.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${application.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Kiểm tra authentication có null không
        if (authentication == null) {
            System.err.println("Authentication is null in CustomAuthenticationSuccessHandler");
            response.sendRedirect(frontendUrl + "/login-error?message=Authentication+is+null");
            return;
        }

        // Kiểm tra xem principal có phải là OidcUser không
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email == null) {
                System.err.println("Could not get email from OidcUser");
                response.sendRedirect(frontendUrl + "/login-error?message=Could+not+get+email");
                return;
            }

            // Tìm user trong DB
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        System.err.println("FATAL: User not found in DB after OIDC login despite CustomOidcUserService: " + email);
                        return new RuntimeException("User not found in DB after OIDC login: " + email);
                    });

            // Tạo JWT
            String jwt = jwtUtil.generateToken(user);

            // Tạo URL redirect về Frontend kèm token trong URL Fragment
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/login-success")
                    .fragment("token=" + jwt)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        } else {
            System.err.println("Authentication principal is not an OidcUser: " + authentication.getPrincipal().getClass().getName());
            response.sendRedirect(frontendUrl + "/login-error?message=Authentication+principal+is+not+OidcUser");
        }
    }
}