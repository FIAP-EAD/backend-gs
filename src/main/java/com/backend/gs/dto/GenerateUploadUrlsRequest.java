package com.backend.gs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GenerateUploadUrlsRequest {
    
    @NotNull(message = "jobReportId is required")
    private Long jobReportId;
    
    @NotNull(message = "numQuestions is required")
    @Min(value = 1, message = "numQuestions must be at least 1")
    private Integer numQuestions;
    
    private String callbackUrl; // URL para callbacks da AWS (opcional)

    public GenerateUploadUrlsRequest() {
    }

    public GenerateUploadUrlsRequest(Long jobReportId, Integer numQuestions) {
        this.jobReportId = jobReportId;
        this.numQuestions = numQuestions;
    }
    
    public GenerateUploadUrlsRequest(Long jobReportId, Integer numQuestions, String callbackUrl) {
        this.jobReportId = jobReportId;
        this.numQuestions = numQuestions;
        this.callbackUrl = callbackUrl;
    }

    public Long getJobReportId() {
        return jobReportId;
    }

    public void setJobReportId(Long jobReportId) {
        this.jobReportId = jobReportId;
    }

    public Integer getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(Integer numQuestions) {
        this.numQuestions = numQuestions;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}

