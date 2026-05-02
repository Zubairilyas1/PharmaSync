package backend.repositories;

import backend.models.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLAuditLogRepository implements AuditLogRepository {
    private Connection connection;

    public MySQLAuditLogRepository(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "action VARCHAR(255) NOT NULL," +
                     "username VARCHAR(100) NOT NULL," +
                     "timestamp DATETIME NOT NULL," +
                     "details TEXT" +
                     ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(AuditLog log) {
        String sql = "INSERT INTO audit_logs (action, username, timestamp, details) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, log.getAction());
            pstmt.setString(2, log.getUsername());
            pstmt.setTimestamp(3, Timestamp.valueOf(log.getTimestamp()));
            pstmt.setString(4, log.getDetails());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AuditLog> findAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(new AuditLog(
                    rs.getInt("id"),
                    rs.getString("action"),
                    rs.getString("username"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getString("details")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}