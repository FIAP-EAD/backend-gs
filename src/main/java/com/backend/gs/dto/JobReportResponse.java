package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobReportResponse {

    @JsonProperty("job_info")
    private String jobInfo;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("job_report_id")
    private Long jobReportId;

    public JobReportResponse(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public JobReportResponse(String jobInfo, String sessionId) {
        this.jobInfo = jobInfo;
        this.sessionId = sessionId;
    }

    public JobReportResponse(String jobInfo, String sessionId, Long jobReportId) {
        this.jobInfo = jobInfo;
        this.sessionId = sessionId;
        this.jobReportId = jobReportId;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getJobReportId() {
        return jobReportId;
    }

    public void setJobReportId(Long jobReportId) {
        this.jobReportId = jobReportId;
    }
}
