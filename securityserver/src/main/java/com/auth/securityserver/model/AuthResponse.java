package com.auth.securityserver.model;

import lombok.Data;

@Data
public class AuthResponse {
    private boolean authenticated;
    private String username; // Optional, if you need to pass user details

    public AuthResponse(boolean authenticated, String username) {
        this.authenticated = authenticated;
        this.username = username;
    }
}
