package com.example.petitionplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private String email;
    private String role;

    public AuthResponse(String token, String type, String email, String role) {
        this.token = token;
        this.type = type;
        this.email = email;
        this.role = role;
    }
}
