package com.backend.gs.utils;

import com.backend.gs.model.JobReport;

public class JobInfoUtil {

    public JobInfoUtil(){}

    public static String buildJobInfo(JobReport jobReport) {
        return String.format(
                "Empresa: %s\nTítulo: %s\nDescrição: %s",
                jobReport.getCompany(),
                jobReport.getTitle(),
                jobReport.getDescription()
        );
    }
}
