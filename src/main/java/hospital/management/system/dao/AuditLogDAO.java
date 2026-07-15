package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogDAO.class);

    public void save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (action, details, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, log.getAction());
            pstmt.setString(2, log.getDetails());
            if (log.getUserId() != null) {
                pstmt.setInt(3, log.getUserId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setLogId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving audit log", e);
            throw new DataAccessException("Failed to save audit log", e);
        }
    }

    public List<AuditLog> findAll() {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC";
        List<AuditLog> list = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                
                int uid = rs.getInt("user_id");
                if (!rs.wasNull()) log.setUserId(uid);
                
                log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(log);
            }
        } catch (SQLException e) {
            logger.error("Error fetching audit logs", e);
            throw new DataAccessException("Failed to fetch audit logs", e);
        }
        return list;
    }
}
