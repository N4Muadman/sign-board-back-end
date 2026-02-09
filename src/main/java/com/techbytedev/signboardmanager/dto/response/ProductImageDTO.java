package com.techbytedev.signboardmanager.dto.response;

import lombok.Data;

@Data
public class ProductImageDTO {
    private int id;
    private String imageUrl;
    private String imageBase64; // Thêm dòng này


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}