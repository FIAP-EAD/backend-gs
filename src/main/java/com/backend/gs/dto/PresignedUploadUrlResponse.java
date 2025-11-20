package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PresignedUploadUrlResponse {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("presigned_url")
    private String presignedUrl;

    @JsonProperty("s3_key")
    private String s3Key;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    public PresignedUploadUrlResponse() {
    }

    public PresignedUploadUrlResponse(String sessionId, String presignedUrl, String s3Key, Integer expiresIn) {
        this.sessionId = sessionId;
        this.presignedUrl = presignedUrl;
        this.s3Key = s3Key;
        this.expiresIn = expiresIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}

