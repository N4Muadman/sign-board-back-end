package com.techbytedev.signboardmanager.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
}
