package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.exception.PermissionException;
import com.techbytedev.signboardmanager.service.UserService;
import com.techbytedev.signboardmanager.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        String username = SecurityUtil.getCurrentUserLogin().orElse("");
        if (!username.isEmpty()) {
            User user = userService.findByUsername(username);
            if (user != null) {
                // Tránh tải Role, sử dụng roleName để kiểm tra
                if (user.getRoleName() != null && !"".equals(user.getRoleName())) {
                    // Giả sử bạn có logic kiểm tra quyền dựa trên roleName (cần cập nhật RoleService hoặc PermissionService)
                    // Hiện tại, chỉ kiểm tra ví dụ
                    boolean isAllowed = false; // Thay bằng logic thực tế từ PermissionService
                    if (!isAllowed) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                    }
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                }
            }
        }
        return true;
    }
}