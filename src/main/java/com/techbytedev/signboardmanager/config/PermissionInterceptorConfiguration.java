package com.techbytedev.signboardmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
            "/api/auth/**",
            "/login/oauth2/**",
            "/api/design/**",
            "/api/categories/**",
            "/api/cms/**",
            "/images/**",
            "/**.hot-update.json",
            "/**.hot-update.js"
        };
        registry.addInterceptor(permissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}