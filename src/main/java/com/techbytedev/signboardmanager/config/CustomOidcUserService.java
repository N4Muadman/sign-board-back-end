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

        // Xử lý thông tin user ở đây (nếu cần)
        // Ví dụ: Tự động đăng ký user mới từ Google
        String email = oidcUser.getEmail();
        User user = userService.findOrCreateUser(email, oidcUser.getFullName());

        // TODO: nếu cần bạn có thể custom authorities...

        return oidcUser;
    }
}
