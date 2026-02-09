package com.techbytedev.signboardmanager.dto.response;

public class UserDesignResponseDTO {
    private Long designId;
    private String designImage;
    private String designLink;
    private String status;
    private String designerFullName;
    private String designerEmail;
    private String designerPhoneNumber;
    private String description;
    private String designImageBase64; // Add this line

    // Constructor
    public UserDesignResponseDTO(Long designId, String designImage, String designLink, String status,
            String description, String designerFullName, String designerEmail, String designerPhoneNumber, String designImageBase64) {
        this.designId = designId;
        this.designImage = designImage;
        this.designLink = designLink;
        this.status = status;
        this.designerFullName = designerFullName;
        this.designerEmail = designerEmail;
        this.designerPhoneNumber = designerPhoneNumber;
        this.description = description;
        this.designImageBase64 = designImageBase64; // Add this line
    }

    // Getter and Setter for designImageBase64
    public String getDesignImageBase64() {
        return designImageBase64;
    }

    public void setDesignImageBase64(String designImageBase64) {
        this.designImageBase64 = designImageBase64;
    }

    
 public  String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    // Getters and Setters
    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public String getDesignImage() {
        return designImage;
    }

    public void setDesignImage(String designImage) {
        this.designImage = designImage;
    }

    public String getDesignLink() {
        return designLink;
    }

    public void setDesignLink(String designLink) {
        this.designLink = designLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesignerFullName() {
        return designerFullName;
    }

    public void setDesignerFullName(String designerFullName) {
        this.designerFullName = designerFullName;
    }

    public String getDesignerEmail() {
        return designerEmail;
    }

    public void setDesignerEmail(String designerEmail) {
        this.designerEmail = designerEmail;
    }

    public String getDesignerPhoneNumber() {
        return designerPhoneNumber;
    }

    public void setDesignerPhoneNumber(String designerPhoneNumber) {
        this.designerPhoneNumber = designerPhoneNumber;
    }
}