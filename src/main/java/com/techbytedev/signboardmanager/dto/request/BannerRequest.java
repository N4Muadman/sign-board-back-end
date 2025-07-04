package com.techbytedev.signboardmanager.dto.request;

import lombok.Data;

@Data
public class BannerRequest {
    private String title;
    private String imageBase64;
    private String description;
    private boolean isActive;
    private Long productId;
}
