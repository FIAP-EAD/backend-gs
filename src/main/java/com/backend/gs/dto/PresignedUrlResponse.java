package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PresignedUrlResponse {

    @JsonProperty("s3_path")
    private String s3Path;

    @JsonProperty("presigned_url")
    private String presignedUrl;

    @JsonProperty("file_name")
    private String fileName;

    public PresignedUrlResponse() {
    }

    public PresignedUrlResponse(String s3Path, String presignedUrl, String fileName) {
        this.s3Path = s3Path;
        this.presignedUrl = presignedUrl;
        this.fileName = fileName;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

