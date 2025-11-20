package com.backend.gs.controller;

import com.backend.gs.dto.JobReportRequest;
import com.backend.gs.dto.JobReportResponse;
import com.backend.gs.service.JobReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobReport")
public class JobReportController {

    private final JobReportService service;

    public JobReportController(JobReportService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<JobReportResponse> create(@RequestBody @Valid JobReportRequest request) {
        JobReportResponse response = service.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getJobReportDetails(@PathVariable Long id) {
        throw new UnsupportedOperationException("Método getJobReportDetails ainda não implementado.");
    }
}