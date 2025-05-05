package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.UserRepository;
import com.techbytedev.signboardmanager.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${application.frontend.url:http://127.0.0.1:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.debug("onAuthenticationSuccess called with request URI: {}", request.getRequestURI());
        if (authentication == null) {
            logger.error("Authentication is null in CustomAuthenticationSuccessHandler");
            response.sendRedirect(frontendUrl + "/login-error?message=Authentication+is+null");
            return;
        }

        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            logger.debug("OidcUser received: {}", oidcUser.getAttributes());
            String email = oidcUser.getEmail();
            if (email == null) {
                logger.error("Could not get email from OidcUser");
                response.sendRedirect(frontendUrl + "/login-error?message=Could+not+get+email");
                return;
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found in DB after OIDC login: {}", email);
                        return new RuntimeException("User not found in DB after OIDC login: " + email);
                    });

            String jwt = jwtUtil.generateToken(user);
            logger.debug("Generated JWT for user {}: {}", email, jwt);

            String redirectPath = request.getRequestURI().contains("canva") ? "/design-start" : "/login-success";
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path(redirectPath)
                    .queryParam("token", jwt)
                    .build().toUriString();
            logger.debug("Redirecting to: {}", redirectUrl);

            response.sendRedirect(redirectUrl);
        } else {
            logger.error("Authentication principal is not an OidcUser: {}", authentication.getPrincipal().getClass().getName());
            response.sendRedirect(frontendUrl + "/login-error?message=Authentication+principal+is+not+OidcUser");
        }
    }
}