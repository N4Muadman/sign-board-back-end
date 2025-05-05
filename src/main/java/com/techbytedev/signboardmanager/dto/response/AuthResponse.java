package com.techbytedev.signboardmanager.dto.response;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserResponse user; // THÊM DÒNG NÀY để có setUser()

   

    public AuthResponse(String token) {
        this.token = token;
    }
}