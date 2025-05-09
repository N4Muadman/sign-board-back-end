package com.techbytedev.signboardmanager.security;

import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.service.PermissionService;
import com.techbytedev.signboardmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class CustomPermissionChecker implements PermissionEvaluator {

    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public CustomPermissionChecker(
            @Lazy UserService userService,
            PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username); // Kiểm tra null
        if (user == null || user.getRole() == null) {
            return false;
        }

        String apiPath = (targetDomainObject instanceof String) ? (String) targetDomainObject : "";
        String method = (permission instanceof String) ? (String) permission : "";

        List<Permission> userPermissions = user.getRole().getPermissions().stream()
                .filter(p -> p.getApiPath().equals(apiPath) && p.getMethod().equals(method))
                .toList();

        return !userPermissions.isEmpty();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}