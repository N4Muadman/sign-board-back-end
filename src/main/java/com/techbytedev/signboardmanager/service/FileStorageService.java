package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.config.FileStorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final FileStorageConfig fileStorageConfig;

    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileName).toAbsolutePath().normalize();
        logger.info("Saving file to: {}", filePath);
        Files.write(filePath, file.getBytes());
        return  fileName;

    }
     public void deleteFile(String fileName) throws IOException {
        if (fileName != null && !fileName.isEmpty()) {
            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileName).toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Deleted file: {}", filePath);
            }
        }
    }
}