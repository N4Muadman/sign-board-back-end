package com.techbytedev.signboardmanager.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean active;
    private Set<PermissionResponseDTO> permissions; // Optional, chỉ điền khi includePermissions = true
}