package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportReadyCallback {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("report_path")
    private String reportPath;

    @JsonProperty("report_url")
    private String reportUrl;

    @JsonProperty("job_report_id")
    private Long jobReportId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public Long getJobReportId() {
        return jobReportId;
    }

    public void setJobReportId(Long jobReportId) {
        this.jobReportId = jobReportId;
    }
}

