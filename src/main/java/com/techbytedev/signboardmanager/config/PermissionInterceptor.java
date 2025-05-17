package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.exception.PermissionException;
import com.techbytedev.signboardmanager.service.PermissionService;
import com.techbytedev.signboardmanager.service.UserService;
import com.techbytedev.signboardmanager.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public PermissionInterceptor(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        String username = SecurityUtil.getCurrentUserLogin().orElse("");
        if (username.isEmpty()) {
            throw new PermissionException("Bạn chưa đăng nhập");
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new PermissionException("Người dùng không tồn tại");
        }

        String roleName = user.getRoleName();
        if (roleName == null || roleName.isEmpty()) {
            throw new PermissionException("Vai trò của bạn không hợp lệ");
        }

        // Chuẩn hóa path với nhiều pattern
        List<String> possiblePaths = Arrays.asList(
            path.replaceAll("/\\d+$", "/{id}"), // /api/admin/permissions/{id}
            path.replaceAll("/\\d+$", "/[0-9]+"), // /api/admin/permissions/[0-9]+
            path.replaceAll("/\\d+$", "/:id"), // /api/admin/permissions/:id
            path // Giữ nguyên path gốc
        );

        // Kiểm tra quyền với từng pattern
        for (String normalizedPath : possiblePaths) {
            logger.debug("Checking permission for role: {}, path: {}, method: {}", roleName, normalizedPath, httpMethod);
            if (permissionService.hasPermission(roleName, normalizedPath, httpMethod)) {
                logger.debug("Permission granted for path: {}", normalizedPath);
                return true;
            }
        }

        logger.warn("Permission denied for role: {}, path: {}, method: {}", roleName, path, httpMethod);
        throw new PermissionException("Bạn không có quyền truy cập endpoint này");
    }
}