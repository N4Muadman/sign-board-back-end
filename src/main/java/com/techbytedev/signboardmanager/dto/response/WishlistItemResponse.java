package com.techbytedev.signboardmanager.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WishlistItemResponse {
    private String productName;
    private String imageUrl;
    private BigDecimal discountedPrice;

    public WishlistItemResponse(String productName, String imageUrl, BigDecimal discountedPrice) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.discountedPrice = discountedPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
