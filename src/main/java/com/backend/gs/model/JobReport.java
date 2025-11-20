package com.backend.gs.model;

public class JobReport {

    private long idJobReport;
    private String company;
    private String title;
    private String description;

    public long getIdJobReport() {
        return idJobReport;
    }

    public void setIdJobReport(long idJobReport) {
        this.idJobReport = idJobReport;
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
}
