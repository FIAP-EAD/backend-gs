package com.backend.gs.model;

public class JobReport {

    private long idJobReport;
    private Long idUser;
    private String company;
    private String title;
    private String description;
    private String sessionId;
    private String reportUrl;

    public long getIdJobReport() {
        return idJobReport;
    }

    public void setIdJobReport(long idJobReport) {
        this.idJobReport = idJobReport;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
