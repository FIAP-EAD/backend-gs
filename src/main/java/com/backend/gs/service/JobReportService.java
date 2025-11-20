package com.backend.gs.service;

import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.model.JobReport;
import com.backend.gs.repository.JobReportRepository;
import com.backend.gs.utils.JobInfoUtil;
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


        JobReport jobReport = new JobReport();
        jobReport.setCompany(request.getCompany());
        jobReport.setTitle(request.getTitle());
        jobReport.setDescription(request.getDescription());

        JobReport saved = repository.save(jobReport);

        sendToLambda(saved);

        String jobInfo = JobInfoUtil.buildJobInfo(saved);

        return new JobReportResponse(jobInfo);
    }

    private void sendToLambda(JobReport report) {
        try {
            String lambdaUrl =
                    "https://lv6bwqn7dfkqulrqquhlz3fhdy0zuzbx.lambda-url.us-east-1.on.aws/";

            restTemplate.postForObject(lambdaUrl, report, Void.class);

        } catch (Exception ignored) {
        }
    }
}