package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        logger.debug("Loading user for OidcUserRequest: {}", userRequest.getClientRegistration().getRegistrationId());
        OidcUser oidcUser = super.loadUser(userRequest);
        logger.debug("OidcUser loaded: {}", oidcUser.getAttributes());

        String email = oidcUser.getEmail();
        if (email == null) {
            logger.error("Email from OidcUser is null");
            throw new IllegalStateException("Email from OidcUser is null");
        }

        userService.findOrCreateUser(email, oidcUser.getFullName());
        logger.debug("User processed: email={}, fullName={}", email, oidcUser.getFullName());

        return oidcUser;
    }
}