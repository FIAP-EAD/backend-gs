package com.backend.gs.controller;

import com.backend.gs.dto.AudiosReadyCallback;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getJobReportDetails(@PathVariable Long id) {
        throw new UnsupportedOperationException("Método getJobReportDetails ainda não implementado.");
    }

    @PostMapping("/callback/audios-ready")
    public ResponseEntity<?> audiosReady(@RequestBody AudiosReadyCallback callback) {
        try {
            Long jobReportId = callback.getJobReportId();
            if (jobReportId == null) {
                return ResponseEntity.badRequest().body("job_report_id is required");
            }

            // Atualiza session_id se fornecido
            if (callback.getSessionId() != null) {
                service.updateSessionId(jobReportId, callback.getSessionId());
            }

            // Salva os paths dos áudios
            if (callback.getAudioFiles() != null && !callback.getAudioFiles().isEmpty()) {
                service.saveAudioPaths(jobReportId, callback.getAudioFiles());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
}