package com.techbytedev.signboardmanager.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class BannerRequest {
    private String title;
    private MultipartFile image; // Đây là ảnh dạng file
    private String description;
    private boolean isActive;
}
