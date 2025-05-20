package com.techbytedev.signboardmanager.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String email;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String address;
    private boolean isActive;
    private Integer roleId; // Changed from roleName to roleId
}