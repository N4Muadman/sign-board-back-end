package com.techbytedev.signboardmanager.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;


@Configuration
public class DotEnvConfig {

    @PostConstruct
    public void loadDotEnv() {
        Dotenv dotenv = Dotenv.configure()
            .directory("./") // Đường dẫn tới file .env (thư mục gốc của dự án)
            .ignoreIfMissing() // Bỏ qua nếu file .env không tồn tại
            .load();

        // Đặt các biến từ .env vào System properties để Spring Boot có thể đọc
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}