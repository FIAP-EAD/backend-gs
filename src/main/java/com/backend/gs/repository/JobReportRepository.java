package com.backend.gs.repository;

import com.backend.gs.model.JobReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobReportRepository extends JpaRepository<JobReport, Long> {
}
