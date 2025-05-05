package com.techbytedev.signboardmanager.dto.request;

import lombok.Data;

@Data
public class SaveDesignRequest {
    private String canvaDesignId;
    private String canvaPreviewUrl;
}