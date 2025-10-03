package com.techbytedev.signboardmanager.dto.response;
import java.util.Set;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserResponse user; // THÊM DÒNG NÀY để có setUser()

    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
    public AuthResponse() {
    }
}
    
