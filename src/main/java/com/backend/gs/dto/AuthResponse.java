package com.backend.gs.dto;

public class AuthResponse {

    private String message;
    private boolean success;
    private Long userId;
    private String username;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public AuthResponse(String message, boolean success, Long userId, String username) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.username = username;
    }

    public AuthResponse(String message, boolean success, Long userId, String username, String token) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

