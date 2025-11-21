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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Value("${lambda.upload.urls:https://mcy4uuho2gkb3ey3f5fz3cko2a0kmcgl.lambda-url.us-east-1.on.aws/}")
    private String lambdaUploadUrlsUrl;

    @Value("${lambda.generate.report.url:}")
    private String lambdaGenerateReportUrl;

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
        
        System.out.println("=== GET STATUS para Job Report " + jobReportId + " ===");
        System.out.println("Áudios encontrados: " + audioFiles.size());
        
        // Remove duplicatas baseado no S3_PATH (mantém apenas o mais recente)
        if (audioFiles.size() > 0) {
            Map<String, AudioFile> uniqueAudios = new LinkedHashMap<>();
            for (AudioFile audio : audioFiles) {
                String s3Path = audio.getS3Path();
                // Se já existe, mantém o mais recente (baseado no CREATED_AT)
                if (!uniqueAudios.containsKey(s3Path) || 
                    (audio.getCreatedAt() != null && uniqueAudios.get(s3Path).getCreatedAt() != null &&
                     audio.getCreatedAt().after(uniqueAudios.get(s3Path).getCreatedAt()))) {
                    uniqueAudios.put(s3Path, audio);
                }
            }
            audioFiles = new ArrayList<>(uniqueAudios.values());
            System.out.println("Áudios únicos após remoção de duplicatas: " + audioFiles.size());
        }
        
        System.out.println("Session ID: " + jobReport.getSessionId());
        
        JobReportStatusResponse.Status status;
        List<PresignedUrlResponse> audioUrls = null;
        String reportUrl = null;

        if (audioFiles.isEmpty()) {
            status = JobReportStatusResponse.Status.PENDING;
            System.out.println("Status: PENDING (nenhum áudio encontrado)");
        } else {
            try {
                audioUrls = generatePresignedUrls(audioFiles);
                System.out.println("URLs pré-assinadas geradas: " + (audioUrls != null ? audioUrls.size() : 0));
            } catch (Exception e) {
                System.err.println("ERRO ao gerar presigned URLs: " + e.getMessage());
                e.printStackTrace();
                // Mesmo com erro, define status como AUDIOS_READY se houver áudios salvos
                audioUrls = new ArrayList<>();
            }
            
            // Verifica se relatório está pronto chamando a Lambda
            if (jobReport.getSessionId() != null && lambdaGenerateReportUrl != null && !lambdaGenerateReportUrl.isEmpty()) {
                try {
                    // Chama Lambda para verificar/gerar relatório
                    reportUrl = checkOrGenerateReport(jobReport.getSessionId());
                    if (reportUrl != null && !reportUrl.isEmpty()) {
                        status = JobReportStatusResponse.Status.REPORT_READY;
                        System.out.println("Status: REPORT_READY");
                    } else {
                        status = JobReportStatusResponse.Status.AUDIOS_READY;
                        System.out.println("Status: AUDIOS_READY (relatório ainda não gerado)");
                    }
                } catch (Exception e) {
                    // Relatório ainda não está pronto ou erro ao chamar Lambda
                    status = JobReportStatusResponse.Status.AUDIOS_READY;
                    System.out.println("Status: AUDIOS_READY (erro ao verificar relatório: " + e.getMessage() + ")");
                }
            } else {
                status = JobReportStatusResponse.Status.AUDIOS_READY;
                System.out.println("Status: AUDIOS_READY (sem session_id ou Lambda não configurada)");
            }
        }

        System.out.println("=== FIM GET STATUS ===");
        return new JobReportStatusResponse(status, audioUrls, reportUrl);
    }

    public List<PresignedUrlResponse> generatePresignedUrls(List<AudioFile> audioFiles) {
        return audioFiles.stream()
                .map(audioFile -> {
                    try {
                        // Chama a Lambda para gerar presigned URL de download
                        String presignedUrl = generatePresignedDownloadUrl(audioFile.getS3Path());
                        return new PresignedUrlResponse(audioFile.getS3Path(), presignedUrl, audioFile.getFileName());
                    } catch (Exception e) {
                        System.err.println("ERRO ao gerar presigned URL para " + audioFile.getS3Path() + ": " + e.getMessage());
                        e.printStackTrace();
                        // Retorna uma resposta sem presigned URL em caso de erro
                        return new PresignedUrlResponse(audioFile.getS3Path(), null, audioFile.getFileName());
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    private String generatePresignedDownloadUrl(String s3Path) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Cria o JSON com s3_path para download
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("s3_path", s3Path);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lambdaPresignedUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to get presigned URL from Lambda: " + response.body());
        }

        // Parse da resposta da Lambda
        String responseBody = response.body();
        try {
            // Lambda Function URL retorna {statusCode: 200, body: "..."}
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(responseBody);
            String bodyStr;
            if (jsonNode.has("body")) {
                bodyStr = jsonNode.get("body").asText();
            } else {
                bodyStr = responseBody;
            }
            
            com.fasterxml.jackson.databind.JsonNode bodyNode = objectMapper.readTree(bodyStr);
            if (bodyNode.has("presigned_url")) {
                return bodyNode.get("presigned_url").asText();
            } else {
                throw new Exception("Lambda response missing presigned_url: " + bodyStr);
            }
        } catch (Exception e) {
            throw new Exception("Failed to parse Lambda response: " + responseBody, e);
        }
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

    public com.backend.gs.dto.GenerateUploadUrlsResponse generateMultipleUploadUrls(Long jobReportId, Integer numQuestions) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Cria o payload para a Lambda
        Map<String, Object> payload = new HashMap<>();
        payload.put("job_report_id", jobReportId);
        payload.put("num_questions", numQuestions);
        
        String jsonBody = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lambdaUploadUrlsUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to get upload URLs from Lambda: " + response.body());
        }

        // Parse da resposta da Lambda
        String responseBody = response.body();
        
        try {
            // A Lambda retorna diretamente o JSON (não tem wrapper statusCode/body)
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            String sessionId = jsonNode.get("session_id").asText();
            Integer expiresIn = jsonNode.has("expires_in") ? jsonNode.get("expires_in").asInt() : 3600;
            
            List<com.backend.gs.dto.GenerateUploadUrlsResponse.UploadUrlInfo> uploadUrls = new ArrayList<>();
            
            com.fasterxml.jackson.databind.JsonNode urlsArray = jsonNode.get("upload_urls");
            if (urlsArray != null && urlsArray.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode urlNode : urlsArray) {
                    com.backend.gs.dto.GenerateUploadUrlsResponse.UploadUrlInfo urlInfo = 
                        new com.backend.gs.dto.GenerateUploadUrlsResponse.UploadUrlInfo(
                            urlNode.get("question_index").asInt(),
                            urlNode.get("presigned_url").asText(),
                            urlNode.get("s3_key").asText()
                        );
                    uploadUrls.add(urlInfo);
                }
            }
            
            return new com.backend.gs.dto.GenerateUploadUrlsResponse(sessionId, uploadUrls, expiresIn);
            
        } catch (Exception e) {
            throw new Exception("Failed to parse Lambda response: " + responseBody, e);
        }
    }

    /**
     * Verifica se o relatório existe ou tenta gerá-lo chamando a Lambda
     * Retorna a URL do relatório se existir, ou null se ainda não foi gerado
     */
    private String checkOrGenerateReport(String sessionId) throws Exception {
        if (lambdaGenerateReportUrl == null || lambdaGenerateReportUrl.isEmpty()) {
            return null;
        }

        HttpClient client = HttpClient.newHttpClient();

        // Payload para a Lambda de relatório
        Map<String, Object> payload = new HashMap<>();
        payload.put("session_id", sessionId);
        
        String jsonBody = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lambdaGenerateReportUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse da resposta
                com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response.body());
                
                // Verifica se tem report_url na resposta
                if (jsonNode.has("report_url")) {
                    return jsonNode.get("report_url").asText();
                } else if (jsonNode.has("report_path")) {
                    // Se retornar s3:// path, precisaria gerar presigned URL
                    // Por enquanto, retorna null (relatório ainda não está pronto)
                    return null;
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao chamar Lambda de relatório: " + e.getMessage());
            return null;
        }
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
