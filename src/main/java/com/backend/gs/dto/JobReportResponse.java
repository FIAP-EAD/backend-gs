package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobReportResponse {

    @JsonProperty("job_info")
    private String jobInfo;

    public JobReportResponse(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }
}
