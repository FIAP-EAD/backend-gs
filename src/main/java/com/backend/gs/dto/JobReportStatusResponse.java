package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class JobReportStatusResponse {

    public enum Status {
        PENDING,
        AUDIOS_READY,
        REPORT_READY,
        COMPLETED
    }

    @JsonProperty("status")
    private Status status;

    @JsonProperty("audio_urls")
    private List<PresignedUrlResponse> audioUrls;

    @JsonProperty("report_url")
    private String reportUrl;

    public JobReportStatusResponse() {
    }

    public JobReportStatusResponse(Status status) {
        this.status = status;
    }

    public JobReportStatusResponse(Status status, List<PresignedUrlResponse> audioUrls, String reportUrl) {
        this.status = status;
        this.audioUrls = audioUrls;
        this.reportUrl = reportUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<PresignedUrlResponse> getAudioUrls() {
        return audioUrls;
    }

    public void setAudioUrls(List<PresignedUrlResponse> audioUrls) {
        this.audioUrls = audioUrls;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}

