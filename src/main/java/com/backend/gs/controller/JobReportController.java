package com.backend.gs.controller;

import com.backend.gs.dto.AudiosReadyCallback;
import com.backend.gs.dto.GenerateUploadUrlsRequest;
import com.backend.gs.dto.GenerateUploadUrlsResponse;
import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.dto.JobReportStatusResponse;
import com.backend.gs.dto.PresignedUrlResponse;
import com.backend.gs.dto.PresignedUploadUrlRequest;
import com.backend.gs.dto.PresignedUploadUrlResponse;
import com.backend.gs.dto.ReportReadyCallback;
import com.backend.gs.service.JobReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobReport")
public class JobReportController {

    private final JobReportService service;

    public JobReportController(JobReportService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid JobReportRequest request) {
        try {
            JobReportResponse response = service.createJobReport(request);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating job report: " + e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(400).body(errors);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getJobReportDetails(@PathVariable Long id) {
        throw new UnsupportedOperationException("Método getJobReportDetails ainda não implementado.");
    }

    @PostMapping("/callback/audios-ready")
    public ResponseEntity<?> audiosReady(@RequestBody AudiosReadyCallback callback) {
        try {
            System.out.println("=== CALLBACK AUDIOS-READY RECEBIDO ===");
            System.out.println("Job Report ID: " + callback.getJobReportId());
            System.out.println("Session ID: " + callback.getSessionId());
            System.out.println("Audio Files: " + callback.getAudioFiles());
            
            Long jobReportId = callback.getJobReportId();
            if (jobReportId == null) {
                System.out.println("ERRO: job_report_id é null");
                return ResponseEntity.badRequest().body("job_report_id is required");
            }

            // Atualiza session_id se fornecido
            if (callback.getSessionId() != null) {
                System.out.println("Atualizando session_id para job report " + jobReportId);
                service.updateSessionId(jobReportId, callback.getSessionId());
            }

            // Salva os paths dos áudios
            if (callback.getAudioFiles() != null && !callback.getAudioFiles().isEmpty()) {
                System.out.println("Salvando " + callback.getAudioFiles().size() + " áudios para job report " + jobReportId);
                service.saveAudioPaths(jobReportId, callback.getAudioFiles());
                System.out.println("Áudios salvos com sucesso!");
            } else {
                System.out.println("AVISO: Nenhum áudio recebido no callback");
            }

            System.out.println("=== CALLBACK PROCESSADO COM SUCESSO ===");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("ERRO ao processar callback: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing callback: " + e.getMessage());
        }
    }

    @PostMapping("/callback/report-ready")
    public ResponseEntity<?> reportReady(@RequestBody ReportReadyCallback callback) {
        try {
            Long jobReportId = callback.getJobReportId();
            if (jobReportId == null) {
                return ResponseEntity.badRequest().body("job_report_id is required");
            }

            // Atualiza session_id se fornecido
            if (callback.getSessionId() != null) {
                service.updateSessionId(jobReportId, callback.getSessionId());
            }

            // TODO: Salvar path do relatório quando implementar entidade Report
            // Por enquanto, apenas retorna OK

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing callback: " + e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<JobReportStatusResponse> getStatus(@PathVariable Long id) {
        try {
            JobReportStatusResponse status = service.getStatus(id);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/audios/{id}/presigned-urls")
    public ResponseEntity<List<PresignedUrlResponse>> getPresignedUrls(@PathVariable Long id) {
        try {
            JobReportStatusResponse status = service.getStatus(id);
            if (status.getAudioUrls() != null) {
                return ResponseEntity.ok(status.getAudioUrls());
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/presigned-upload-url")
    public ResponseEntity<PresignedUploadUrlResponse> getPresignedUploadUrl(
            @RequestBody PresignedUploadUrlRequest request) {
        try {
            if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String filename = request.getFilename();
            if (filename == null || filename.isEmpty()) {
                filename = "resposta_" + System.currentTimeMillis() + ".mp3";
            }

            PresignedUploadUrlResponse response = service.generatePresignedUploadUrl(
                    request.getSessionId(), 
                    filename
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/generate-upload-urls")
    public ResponseEntity<?> generateUploadUrls(@RequestBody @Valid GenerateUploadUrlsRequest request) {
        try {
            System.out.println("=== GENERATE UPLOAD URLS ===");
            System.out.println("Job Report ID: " + request.getJobReportId());
            System.out.println("Num Questions: " + request.getNumQuestions());
            
            GenerateUploadUrlsResponse response = service.generateMultipleUploadUrls(
                    request.getJobReportId(), 
                    request.getNumQuestions()
            );
            
            System.out.println("✅ URLs geradas com sucesso!");
            System.out.println("Session ID: " + response.getSessionId());
            System.out.println("Total URLs: " + response.getUploadUrls().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erro ao gerar URLs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to generate upload URLs",
                "message", e.getMessage()
            ));
        }
    }
}