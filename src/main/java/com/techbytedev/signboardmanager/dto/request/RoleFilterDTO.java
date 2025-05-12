package com.techbytedev.signboardmanager.dto.request;

import lombok.Data;

@Data
public class RoleFilterDTO {
    private String name;
    private Boolean active;
    private String description;
}