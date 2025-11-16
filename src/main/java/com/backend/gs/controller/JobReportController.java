package com.backend.gs.controller;

import com.backend.gs.model.JobReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobReport")
public class JobReportController {

    @PostMapping("/create")
    public ResponseEntity<?> createJobReport(@RequestBody JobReport request) {
        throw new UnsupportedOperationException("Método createJobReport ainda não implementado.");
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getJobReportDetails(@PathVariable Long id) {
        throw new UnsupportedOperationException("Método getJobReportDetails ainda não implementado.");
    }
}
