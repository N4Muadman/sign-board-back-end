package com.techbytedev.signboardmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.signboardmanager.dto.response.AuthResponse;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true) // Đảm bảo chỉ đọc, không sửa đổi
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final JwtUtil jwtUtil;

    @Value("${application.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.debug("onAuthenticationSuccess called with request URI: {}", request.getRequestURI());
        if (authentication == null) {
            logger.error("Authentication is null in CustomAuthenticationSuccessHandler");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is null");
            return;
        }

        if (authentication.getPrincipal() instanceof CustomOidcUser customOidcUser) {
            logger.debug("CustomOidcUser received: {}", customOidcUser.getAttributes());
            String email = customOidcUser.getEmail();
            if (email == null) {
                logger.error("Could not get email from CustomOidcUser");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not get email");
                return;
            }

            User user = customOidcUser.getUser();
            if (user == null) {
                logger.error("User object is null in CustomOidcUser for email: {}", email);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User object is null");
                return;
            }

            // Tạo UserResponse mà không truy cập Role hoặc permissions trực tiếp
            String jwt = jwtUtil.generateToken(user);
            logger.debug("Generated JWT for user {}: {}", email, jwt);

            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setFullName(user.getFullName());
            userResponse.setPhoneNumber(user.getPhoneNumber());
            userResponse.setAddress(user.getAddress());
            userResponse.setActive(user.isActive());
            userResponse.setRoleName(user.getRoleName()); // Sử dụng roleName đã đồng bộ từ User

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwt);
            authResponse.setUser(userResponse);

            response.setContentType("application/json;charset=UTF-8"); // Đảm bảo UTF-8
            response.setStatus(HttpServletResponse.SC_OK);
            new ObjectMapper().writeValue(response.getWriter(), authResponse);
        } else {
            logger.error("Authentication principal is not a CustomOidcUser: {}", authentication.getPrincipal().getClass().getName());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication principal is not CustomOidcUser");
        }
    }
}