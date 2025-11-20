package com.backend.gs.service;

import com.backend.gs.dao.JobReportDAO;
import com.backend.gs.model.JobReport;
import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.utils.JobInfoUtil;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class JobReportService {

    private final JobReportDAO jobReportDAO;
    private final JobInfoUtil jobInfoUtil;

    private static final String LAMBDA_URL =
            "https://lv6bwqn7dfkqulrqquhlz3fhdy0zuzbx.lambda-url.us-east-1.on.aws/";

    public JobReportService() {
        this.jobReportDAO = new JobReportDAO();
        this.jobInfoUtil = new JobInfoUtil();
    }

    public JobReportResponse createJobReport(JobReportRequest request) throws Exception {
        JobReport jobReport = new JobReport();
        jobReport.setCompany(request.getCompany());
        jobReport.setTitle(request.getTitle());
        jobReport.setDescription(request.getDescription());

        jobReportDAO.save(jobReport);

        String jobInfo = jobInfoUtil.buildJobInfo(jobReport);

        sendToLambda(jobInfo);

        return new JobReportResponse(jobInfo);
    }

    private void sendToLambda(String jobInfo) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String jsonBody = String.format("{\"job_info\": \"%s\"}",
                jobInfo.replace("\"", "\\\"").replace("\n", "\\n")
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LAMBDA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}