package com.techbytedev.signboardmanager.dto.response;

import java.util.Set;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private boolean isActive;
    private String roleName;
    private Set<PermissionResponseDTO> permissions;
}
