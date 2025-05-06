package com.techbytedev.signboardmanager.config;

import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserService userService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.debug("Loading OIDC user for registration: {}", registrationId);

        try {
            OidcUser oidcUser = super.loadUser(userRequest);
            logger.debug("OidcUser loaded successfully from provider '{}': Attributes received: {}", registrationId, oidcUser.getAttributes());

            String email = oidcUser.getEmail();
            if (email == null || email.isBlank()) {
                logger.error("Email claim is missing or empty in OidcUser from provider '{}'. Attributes: {}", registrationId, oidcUser.getAttributes());
                throw new OAuth2AuthenticationException("Email not found in OIDC user information from " + registrationId);
            }

            String fullName = oidcUser.getFullName();
            if (fullName == null || fullName.isBlank()) {
                fullName = oidcUser.getGivenName() != null ? oidcUser.getGivenName() : "";
                if (oidcUser.getFamilyName() != null) {
                    fullName += (!fullName.isEmpty() ? " " : "") + oidcUser.getFamilyName();
                }
                if (fullName.isBlank()) {
                    fullName = email;
                    logger.warn("Full name claim is missing for email '{}' from provider '{}'. Using email as name.", email, registrationId);
                } else {
                    logger.debug("Constructed full name '{}' for email '{}' from provider '{}'.", fullName, email, registrationId);
                }
            }

            User user = userService.findOrCreateUser(email, fullName);
            logger.info("Successfully processed user (found or created) for OIDC login: email={}, userId={}, provider={}", email, user.getId(), registrationId);
            return new CustomOidcUser(oidcUser, user);
        } catch (OAuth2AuthenticationException e) {
            logger.error("OAuth2 authentication failed for provider '{}': {}", registrationId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to find or create user in local DB for provider '{}': {}", registrationId, e.getMessage(), e);
            throw new OAuth2AuthenticationException(new OAuth2Error("server_error", "Failed to process user in local database", null), e);
        }
    }
}