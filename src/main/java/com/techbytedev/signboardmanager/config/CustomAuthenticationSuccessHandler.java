package com.techbytedev.signboardmanager.config; // Đảm bảo đúng package

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.UserRepository; // Sử dụng UserRepository hoặc UserService
import com.techbytedev.signboardmanager.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler; // Có thể dùng SimpleUrl nếu muốn đơn giản hơn hoặc kế thừa nó
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Hoặc dùng constructor injection thủ công
public class CustomAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler { // Implement interface gốc

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // Inject UserRepository hoặc UserService

    // Đọc URL frontend từ application.properties
    // Đảm bảo bạn có dòng `application.frontend.url=http://localhost:3000` trong properties
    @Value("${application.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Kiểm tra xem principal có phải là OidcUser không
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email == null) {
                 // Xử lý trường hợp không lấy được email
                 System.err.println("Could not get email from OidcUser");
                 response.sendRedirect(frontendUrl + "/login-error?message=Could+not+get+email");
                 return;
            }


            // Tìm user trong DB (đã được đảm bảo tồn tại bởi CustomOidcUserService)
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                         // Log lỗi này vì nó không nên xảy ra nếu CustomOidcUserService hoạt động đúng
                         System.err.println("FATAL: User not found in DB after OIDC login despite CustomOidcUserService: " + email);
                         return new RuntimeException("User not found in DB after OIDC login: " + email);
                    });

            // Tạo JWT
            // Đảm bảo User entity hoặc đối tượng bạn dùng có đủ thông tin cho generateToken
            String jwt = jwtUtil.generateToken(user);

            // Tạo URL redirect về Frontend kèm token trong URL Fragment (#)
            // Đảm bảo frontend có route '/login-success' để xử lý
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/login-success") // Đường dẫn trên frontend để xử lý token
                    .fragment("token=" + jwt) // Thêm token vào phần fragment (#token=...)
                    .build().toUriString();

            // Quan trọng: Xóa các thuộc tính session không cần thiết (nếu có) để tránh lỗi sau này
            // request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST"); // Ví dụ

            // Thực hiện redirect về frontend
            // Sử dụng getRedirectStrategy() nếu kế thừa từ SimpleUrlAuthenticationSuccessHandler
            response.sendRedirect(redirectUrl);

        } else {
            // Nếu principal không phải OidcUser (bất thường với Google OIDC)
             System.err.println("Authentication principal is not an OidcUser: " + authentication.getPrincipal().getClass().getName());
             response.sendRedirect(frontendUrl + "/login-error?message=Authentication+principal+is+not+OidcUser");
        }
    }
}
