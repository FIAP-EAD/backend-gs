package com.backend.gs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AudiosReadyCallback {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("audio_files")
    private List<String> audioFiles;

    @JsonProperty("job_report_id")
    private Long jobReportId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getAudioFiles() {
        return audioFiles;
    }

    public void setAudioFiles(List<String> audioFiles) {
        this.audioFiles = audioFiles;
    }

    public Long getJobReportId() {
        return jobReportId;
    }

    public void setJobReportId(Long jobReportId) {
        this.jobReportId = jobReportId;
    }
}

