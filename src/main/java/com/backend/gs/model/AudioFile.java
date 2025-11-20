package com.backend.gs.model;

import java.sql.Timestamp;

public class AudioFile {

    private long idAudioFile;
    private long idJobReport;
    private String s3Path;
    private String fileName;
    private Timestamp createdAt;

    public long getIdAudioFile() {
        return idAudioFile;
    }

    public void setIdAudioFile(long idAudioFile) {
        this.idAudioFile = idAudioFile;
    }

    public long getIdJobReport() {
        return idJobReport;
    }

    public void setIdJobReport(long idJobReport) {
        this.idJobReport = idJobReport;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

