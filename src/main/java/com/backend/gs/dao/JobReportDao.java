package com.backend.gs.dao;

import com.backend.gs.database.OracleConnection;
import com.backend.gs.model.JobReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JobReportDao {

    @Autowired
    private OracleConnection oracleConnection;

    public JobReport save(JobReport jobReport) throws SQLException {
        String sql = "INSERT INTO JOB_REPORT (ID_USER, COMPANY, TITLE, DESCRIPTION) VALUES (?, ?, ?, ?)";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, jobReport.getIdUser());
            stmt.setString(2, jobReport.getCompany());
            stmt.setString(3, jobReport.getTitle());
            stmt.setString(4, jobReport.getDescription());

            stmt.executeUpdate();

            // Busca o ID do job report recém-criado (Oracle não suporta getGeneratedKeys da mesma forma)
            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT ID_JOB_REPORT FROM JOB_REPORT WHERE COMPANY = ? AND TITLE = ? ORDER BY ID_JOB_REPORT DESC FETCH FIRST 1 ROWS ONLY")) {
                selectStmt.setString(1, jobReport.getCompany());
                selectStmt.setString(2, jobReport.getTitle());
                try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    jobReport.setIdJobReport(rs.getLong(1));
                    }
                }
            }

            return jobReport;
        }
    }

    public JobReport findById(long id) throws SQLException {
        String sql = "SELECT ID_JOB_REPORT, ID_USER, COMPANY, TITLE, DESCRIPTION, SESSION_ID FROM JOB_REPORT WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JobReport job = new JobReport();
                    job.setIdJobReport(rs.getLong("ID_JOB_REPORT"));
                    job.setIdUser(rs.getLong("ID_USER"));
                    job.setCompany(rs.getString("COMPANY"));
                    job.setTitle(rs.getString("TITLE"));
                    job.setDescription(rs.getString("DESCRIPTION"));
                    job.setSessionId(rs.getString("SESSION_ID"));
                    // Tenta ler REPORT_URL se existir
                    try {
                        job.setReportUrl(rs.getString("REPORT_URL"));
                    } catch (SQLException e) {
                        // Coluna ainda não existe, ignora
                        job.setReportUrl(null);
                    }
                    return job;
                }
            }

            return null; // não encontrado
        }
    }

    public List<JobReport> findAll() throws SQLException {
        String sql = "SELECT ID_JOB_REPORT, ID_USER, COMPANY, TITLE, DESCRIPTION, SESSION_ID FROM JOB_REPORT";

        List<JobReport> list = new ArrayList<>();

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JobReport job = new JobReport();
                job.setIdJobReport(rs.getLong("ID_JOB_REPORT"));
                job.setIdUser(rs.getLong("ID_USER"));
                job.setCompany(rs.getString("COMPANY"));
                job.setTitle(rs.getString("TITLE"));
                job.setDescription(rs.getString("DESCRIPTION"));
                job.setSessionId(rs.getString("SESSION_ID"));
                // Tenta ler REPORT_URL se existir
                try {
                    job.setReportUrl(rs.getString("REPORT_URL"));
                } catch (SQLException e) {
                    // Coluna ainda não existe, ignora
                    job.setReportUrl(null);
                }
                list.add(job);
            }
        }

        return list;
    }

    public boolean update(JobReport jobReport) throws SQLException {
        String sql = "UPDATE JOB_REPORT SET COMPANY = ?, TITLE = ?, DESCRIPTION = ? WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobReport.getCompany());
            stmt.setString(2, jobReport.getTitle());
            stmt.setString(3, jobReport.getDescription());
            stmt.setLong(4, jobReport.getIdJobReport());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM JOB_REPORT WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean updateSessionId(long id, String sessionId) throws SQLException {
        String sql = "UPDATE JOB_REPORT SET SESSION_ID = ? WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            stmt.setLong(2, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean updateReportUrl(long id, String reportUrl) throws SQLException {
        String sql = "UPDATE JOB_REPORT SET REPORT_URL = ? WHERE ID_JOB_REPORT = ?";

        try (Connection conn = oracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reportUrl);
            stmt.setLong(2, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
    
    /**
     * Altera o tamanho da coluna REPORT_URL para suportar URLs longas da AWS
     * Execute este método apenas uma vez para fazer a migração
     */
    public void alterReportUrlColumnSize() throws SQLException {
        String sql = "ALTER TABLE JOB_REPORT MODIFY REPORT_URL VARCHAR2(2000)";
        
        try (Connection conn = oracleConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("✅ Coluna REPORT_URL alterada com sucesso para VARCHAR2(2000)");
        }
    }
}
