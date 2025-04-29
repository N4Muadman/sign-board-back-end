package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Đảm bảo email không null
        String email = oidcUser.getEmail();
        if (email == null) {
            throw new IllegalStateException("Email from OidcUser is null");
        }

        // Tự động đăng ký user mới từ Google
        userService.findOrCreateUser(email, oidcUser.getFullName());

        return oidcUser;
    }
}