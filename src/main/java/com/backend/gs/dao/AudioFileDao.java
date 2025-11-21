package com.backend.gs.dao;

import com.backend.gs.database.OracleConnection;
import com.backend.gs.model.AudioFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AudioFileDao {

    @Autowired
    private OracleConnection oracleConnection;

    public AudioFile save(long jobReportId, String s3Path, String fileName) throws SQLException {
        String sql = "INSERT INTO AUDIO_FILES (ID_JOB_REPORT, S3_PATH, FILE_NAME, CREATED_AT) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, jobReportId);
            stmt.setString(2, s3Path);
            stmt.setString(3, fileName);

            stmt.executeUpdate();

            // Busca o ID do áudio recém-criado (Oracle não suporta getGeneratedKeys da mesma forma)
            AudioFile audioFile = new AudioFile();
            audioFile.setIdJobReport(jobReportId);
            audioFile.setS3Path(s3Path);
            audioFile.setFileName(fileName);
            
            // Busca o ID gerado
            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT ID_AUDIO_FILE, CREATED_AT FROM AUDIO_FILES WHERE ID_JOB_REPORT = ? AND S3_PATH = ? ORDER BY CREATED_AT DESC FETCH FIRST 1 ROWS ONLY")) {
                selectStmt.setLong(1, jobReportId);
                selectStmt.setString(2, s3Path);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        audioFile.setIdAudioFile(rs.getLong("ID_AUDIO_FILE"));
                        audioFile.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    }
                }
            }

            return audioFile;
        }
    }

    public List<AudioFile> findByJobReportId(long jobReportId) throws SQLException {
        String sql = "SELECT ID_AUDIO_FILE, ID_JOB_REPORT, S3_PATH, FILE_NAME, CREATED_AT FROM AUDIO_FILES WHERE ID_JOB_REPORT = ? ORDER BY CREATED_AT";

        List<AudioFile> list = new ArrayList<>();

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, jobReportId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AudioFile audioFile = new AudioFile();
                    audioFile.setIdAudioFile(rs.getLong("ID_AUDIO_FILE"));
                    audioFile.setIdJobReport(rs.getLong("ID_JOB_REPORT"));
                    audioFile.setS3Path(rs.getString("S3_PATH"));
                    audioFile.setFileName(rs.getString("FILE_NAME"));
                    audioFile.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    list.add(audioFile);
                }
            }
        }

        return list;
    }

    public boolean deleteByJobReportId(long jobReportId) throws SQLException {
        String sql = "DELETE FROM AUDIO_FILES WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, jobReportId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
}

