package com.backend.gs.dto;

import java.util.List;

public class GenerateUploadUrlsResponse {
    
    private String sessionId;
    private List<UploadUrlInfo> uploadUrls;
    private Integer expiresIn;

    public GenerateUploadUrlsResponse() {
    }

    public GenerateUploadUrlsResponse(String sessionId, List<UploadUrlInfo> uploadUrls, Integer expiresIn) {
        this.sessionId = sessionId;
        this.uploadUrls = uploadUrls;
        this.expiresIn = expiresIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<UploadUrlInfo> getUploadUrls() {
        return uploadUrls;
    }

    public void setUploadUrls(List<UploadUrlInfo> uploadUrls) {
        this.uploadUrls = uploadUrls;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static class UploadUrlInfo {
        private Integer questionIndex;
        private String presignedUrl;
        private String s3Key;

        public UploadUrlInfo() {
        }

        public UploadUrlInfo(Integer questionIndex, String presignedUrl, String s3Key) {
            this.questionIndex = questionIndex;
            this.presignedUrl = presignedUrl;
            this.s3Key = s3Key;
        }

        public Integer getQuestionIndex() {
            return questionIndex;
        }

        public void setQuestionIndex(Integer questionIndex) {
            this.questionIndex = questionIndex;
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
    }
}

