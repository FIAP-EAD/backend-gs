package com.backend.gs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class JobReportRequest {
    @NotBlank(message = "O nome da empresa não pode estar vazio.")
    @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ0-9 .,&-]+$",
            message = "O nome da empresa contém caracteres inválidos."
    )
    private String company;

    @NotBlank(message = "O título não pode estar vazio.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    private String title;

    @NotBlank(message = "A descrição não pode estar vazia.")
    @Size(min = 50, message = "A descrição deve ter no mínimo 50 caracteres.")
    private String description;

    private String callbackUrl;

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

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}