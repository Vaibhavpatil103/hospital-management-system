package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.MedicalHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(MedicalHistoryDAO.class);

    public List<MedicalHistory> findByPatientId(int patientId) {
        String sql = "SELECT * FROM medical_history WHERE patient_id = ? ORDER BY recorded_date DESC";
        List<MedicalHistory> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding medical history", e);
            throw new DataAccessException("Failed to fetch medical history", e);
        }
        return list;
    }

    public void save(MedicalHistory history) {
        String sql = "INSERT INTO medical_history (patient_id, diagnosis, allergies, notes, recorded_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, history.getPatientId());
            pstmt.setString(2, history.getDiagnosis());
            pstmt.setString(3, history.getAllergies());
            pstmt.setString(4, history.getNotes());
            pstmt.setDate(5, Date.valueOf(history.getRecordedDate()));
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) history.setHistoryId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Error saving medical history", e);
            throw new DataAccessException("Failed to save medical history", e);
        }
    }

    private MedicalHistory mapResultSet(ResultSet rs) throws SQLException {
        MedicalHistory m = new MedicalHistory();
        m.setHistoryId(rs.getInt("history_id"));
        m.setPatientId(rs.getInt("patient_id"));
        m.setDiagnosis(rs.getString("diagnosis"));
        m.setAllergies(rs.getString("allergies"));
        m.setNotes(rs.getString("notes"));
        m.setRecordedDate(rs.getDate("recorded_date").toLocalDate());
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return m;
    }
}
