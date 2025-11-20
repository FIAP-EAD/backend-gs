package com.backend.gs.service;

import com.backend.gs.dao.AudioFileDao;
import com.backend.gs.dao.JobReportDao;
import com.backend.gs.dto.JobReportStatusResponse;
import com.backend.gs.dto.PresignedUrlResponse;
import com.backend.gs.dto.PresignedUploadUrlResponse;
import com.backend.gs.model.AudioFile;
import com.backend.gs.model.JobReport;
import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.utils.JobInfoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobReportService {

    private final JobReportDao jobReportDAO;
    private final AudioFileDao audioFileDao;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    @Value("${lambda.url:https://lv6bwqn7dfkqulrqquhlz3fhdy0zuzbx.lambda-url.us-east-1.on.aws/}")
    private String lambdaUrl;

    @Value("${lambda.presigned.url:https://6t7s4lvjy7aohaxruak6a3arfy0byiau.lambda-url.us-east-1.on.aws/}")
    private String lambdaPresignedUrl;

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    @Autowired
    public JobReportService(JobReportDao jobReportDAO, AudioFileDao audioFileDao, 
                           S3Service s3Service) {
        this.jobReportDAO = jobReportDAO;
        this.audioFileDao = audioFileDao;
        this.s3Service = s3Service;
        this.objectMapper = new ObjectMapper();
    }

    public JobReportResponse createJobReport(JobReportRequest request) throws Exception {
        JobReport jobReport = new JobReport();
        jobReport.setCompany(request.getCompany());
        jobReport.setTitle(request.getTitle());
        jobReport.setDescription(request.getDescription());

        jobReportDAO.save(jobReport);

        String jobInfo = JobInfoUtil.buildJobInfo(jobReport);

        String sessionId = sendToLambda(jobInfo, request.getCallbackUrl(), jobReport.getIdJobReport());

        if (sessionId != null) {
            jobReportDAO.updateSessionId(jobReport.getIdJobReport(), sessionId);
            return new JobReportResponse(jobInfo, sessionId, jobReport.getIdJobReport());
        }

        return new JobReportResponse(jobInfo, null, jobReport.getIdJobReport());
    }

    private String sendToLambda(String jobInfo, String callbackUrl, Long jobReportId) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Cria o JSON com job_info, callback_url e job_report_id
        String jsonBody = objectMapper.writeValueAsString(new LambdaRequest(jobInfo, callbackUrl, jobReportId));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lambdaUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Tenta extrair session_id da resposta
        if (response.statusCode() == 200) {
            try {
                LambdaResponse lambdaResponse = objectMapper.readValue(response.body(), LambdaResponse.class);
                return lambdaResponse.getSessionId();
            } catch (Exception e) {
                // Se não conseguir parsear, retorna null
                return null;
            }
        }
        
        return null;
    }

    public void saveAudioPaths(Long jobReportId, List<String> audioFiles) throws Exception {
        for (String s3Path : audioFiles) {
            // Extrai o nome do arquivo do path
            String fileName = s3Path.substring(s3Path.lastIndexOf('/') + 1);
            audioFileDao.save(jobReportId, s3Path, fileName);
        }
    }

    public JobReportStatusResponse getStatus(Long jobReportId) throws Exception {
        JobReport jobReport = jobReportDAO.findById(jobReportId);
        if (jobReport == null) {
            throw new IllegalArgumentException("Job report not found: " + jobReportId);
        }

        List<AudioFile> audioFiles = audioFileDao.findByJobReportId(jobReportId);
        
        JobReportStatusResponse.Status status;
        List<PresignedUrlResponse> audioUrls = null;
        String reportUrl = null;

        if (audioFiles.isEmpty()) {
            status = JobReportStatusResponse.Status.PENDING;
        } else {
            audioUrls = generatePresignedUrls(audioFiles);
            
            // Verifica se relatório está pronto no S3
            if (jobReport.getSessionId() != null) {
                String reportS3Path = "s3://" + s3BucketName + "/reports/" + jobReport.getSessionId() + "/report.json";
                try {
                    // Tenta gerar URL pré-assinada do relatório
                    String bucket = s3Service.extractBucket(reportS3Path);
                    String key = s3Service.extractKey(reportS3Path);
                    reportUrl = s3Service.generatePresignedUrl(bucket, key, 3600);
                    status = JobReportStatusResponse.Status.REPORT_READY;
                } catch (Exception e) {
                    // Relatório ainda não está pronto
                    status = JobReportStatusResponse.Status.AUDIOS_READY;
                }
            } else {
                status = JobReportStatusResponse.Status.AUDIOS_READY;
            }
        }

        return new JobReportStatusResponse(status, audioUrls, reportUrl);
    }

    public List<PresignedUrlResponse> generatePresignedUrls(List<AudioFile> audioFiles) {
        return audioFiles.stream()
                .map(audioFile -> {
                    String bucket = s3Service.extractBucket(audioFile.getS3Path());
                    String key = s3Service.extractKey(audioFile.getS3Path());
                    String presignedUrl = s3Service.generatePresignedUrl(bucket, key, 3600);
                    return new PresignedUrlResponse(audioFile.getS3Path(), presignedUrl, audioFile.getFileName());
                })
                .collect(Collectors.toList());
    }

    public void updateSessionId(Long jobReportId, String sessionId) throws Exception {
        jobReportDAO.updateSessionId(jobReportId, sessionId);
    }

    public PresignedUploadUrlResponse generatePresignedUploadUrl(String sessionId, String filename) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Cria o JSON com session_id e filename
        String jsonBody = objectMapper.writeValueAsString(new PresignedUrlLambdaRequest(sessionId, filename));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lambdaPresignedUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to get presigned URL from Lambda: " + response.body());
        }

        // Parse da resposta da Lambda (Lambda Function URL retorna {statusCode, body})
        String responseBody = response.body();
        PresignedUrlLambdaResponse lambdaResponse;
        
        try {
            // Lambda Function URL retorna {statusCode: 200, body: "..."}
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.has("body")) {
                // O body pode ser uma string JSON ou um objeto JSON
                String bodyStr = jsonNode.get("body").asText();
                lambdaResponse = objectMapper.readValue(bodyStr, PresignedUrlLambdaResponse.class);
            } else {
                // Se não tiver campo body, tenta parsear diretamente
                lambdaResponse = objectMapper.readValue(responseBody, PresignedUrlLambdaResponse.class);
            }
        } catch (Exception e) {
            throw new Exception("Failed to parse Lambda response: " + responseBody, e);
        }
        
        return new PresignedUploadUrlResponse(
                lambdaResponse.getSession_id(),
                lambdaResponse.getPresigned_url(),
                lambdaResponse.getS3_key(),
                lambdaResponse.getExpires_in()
        );
    }

    // Classes auxiliares para serialização JSON
    private static class LambdaRequest {
        private String job_info;
        private String callback_url;
        private Long job_report_id;

        public LambdaRequest(String job_info, String callback_url, Long job_report_id) {
            this.job_info = job_info;
            this.callback_url = callback_url;
            this.job_report_id = job_report_id;
        }

        public String getJob_info() { return job_info; }
        public String getCallback_url() { return callback_url; }
        public Long getJob_report_id() { return job_report_id; }
    }

    private static class LambdaResponse {
        private String session_id;

        public String getSessionId() { return session_id; }
        public void setSession_id(String session_id) { this.session_id = session_id; }
    }

    private static class PresignedUrlLambdaRequest {
        private String session_id;
        private String filename;

        public PresignedUrlLambdaRequest(String session_id, String filename) {
            this.session_id = session_id;
            this.filename = filename;
        }

        public String getSession_id() { return session_id; }
        public String getFilename() { return filename; }
    }

    private static class PresignedUrlLambdaResponse {
        private String session_id;
        private String presigned_url;
        private String s3_key;
        private Integer expires_in;

        public String getSession_id() { return session_id; }
        public void setSession_id(String session_id) { this.session_id = session_id; }
        public String getPresigned_url() { return presigned_url; }
        public void setPresigned_url(String presigned_url) { this.presigned_url = presigned_url; }
        public String getS3_key() { return s3_key; }
        public void setS3_key(String s3_key) { this.s3_key = s3_key; }
        public Integer getExpires_in() { return expires_in; }
        public void setExpires_in(Integer expires_in) { this.expires_in = expires_in; }
    }
}
