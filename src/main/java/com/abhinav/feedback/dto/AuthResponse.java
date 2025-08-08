package com.abhinav.feedback.dto;

public class AuthResponse {
    private String token;
    private String username; // Optional, but often useful to return

    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Getters and Setters (or use Lombok's @Data)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
