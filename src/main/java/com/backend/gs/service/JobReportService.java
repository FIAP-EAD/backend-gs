package com.backend.gs.service;

import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.model.JobReport;
import com.backend.gs.repository.JobReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JobReportService {

    private final JobReportRepository repository;
    private final RestTemplate restTemplate;

    public JobReportService(JobReportRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public JobReportResponse create(JobReportRequest request) {

        if (request.getCompany().length() < 2) {
            throw new IllegalArgumentException("Nome da empresa muito curto.");
        }

        JobReport jobReport = new JobReport();
        jobReport.setCompany(request.getCompany());
        jobReport.setTitle(request.getTitle());
        jobReport.setDescription(request.getDescription());

        JobReport saved = repository.save(jobReport);

        boolean sent = sendToFakeAWS(saved);

        JobReportResponse response = new JobReportResponse();
        response.setId(saved.getIdJobReport());
        response.setCompany(saved.getCompany());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setSentToAWS(sent);

        return response;
    }

    private boolean sendToFakeAWS(JobReport report) {
        try {
            String url = "https://aws.fake-endpoint.com/sendReport";

            restTemplate.postForObject(url, report, Void.class);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}