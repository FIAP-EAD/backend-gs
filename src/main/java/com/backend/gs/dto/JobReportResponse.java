package com.backend.gs.dto;

public class JobReportResponse {
    private Long id;
    private String company;
    private String title;
    private String description;
    private boolean sentToAWS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isSentToAWS() {
        return sentToAWS;
    }

    public void setSentToAWS(boolean sentToAWS) {
        this.sentToAWS = sentToAWS;
    }
}
