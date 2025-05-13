package com.techbytedev.signboardmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);

    @Value("${upload.dir}")
    private String uploadDir;


    @PostConstruct
    public void init() {
        try {
            logger.info("Configuring upload directory: {}", uploadDir);
            if (uploadDir == null || uploadDir.trim().isEmpty()) {
                throw new IllegalArgumentException("Upload directory is not specified in configuration");
            }
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                logger.info("Creating upload directory at: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create upload directory: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tạo thư mục uploads: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid upload.dir configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Cấu hình upload.dir không hợp lệ: " + e.getMessage(), e);
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}