package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PresignedUploadUrlRequest {

    @JsonProperty("session_id")
    private String sessionId;

    private String filename;

    public PresignedUploadUrlRequest() {
    }

    public PresignedUploadUrlRequest(String sessionId, String filename) {
        this.sessionId = sessionId;
        this.filename = filename;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

