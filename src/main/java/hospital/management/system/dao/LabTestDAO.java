package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.LabTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabTestDAO {
    private static final Logger logger = LoggerFactory.getLogger(LabTestDAO.class);

    public List<LabTest> findByPatientId(int patientId) {
        String sql = "SELECT * FROM lab_tests WHERE patient_id = ? ORDER BY test_date DESC";
        List<LabTest> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding lab tests", e);
            throw new DataAccessException("Failed to fetch lab tests", e);
        }
        return list;
    }

    public void save(LabTest t) {
        String sql = "INSERT INTO lab_tests (patient_id, doctor_id, test_name, test_result, test_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, t.getPatientId());
            if (t.getDoctorId() != null) {
                pstmt.setInt(2, t.getDoctorId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, t.getTestName());
            pstmt.setString(4, t.getTestResult());
            pstmt.setDate(5, Date.valueOf(t.getTestDate()));
            pstmt.setString(6, t.getStatus());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) t.setTestId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Error saving lab test", e);
            throw new DataAccessException("Failed to save lab test", e);
        }
    }

    public void updateResult(int testId, String result, String status) {
        String sql = "UPDATE lab_tests SET test_result = ?, status = ? WHERE test_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, result);
            pstmt.setString(2, status);
            pstmt.setInt(3, testId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating lab test", e);
            throw new DataAccessException("Failed to update lab test", e);
        }
    }

    private LabTest mapResultSet(ResultSet rs) throws SQLException {
        LabTest t = new LabTest();
        t.setTestId(rs.getInt("test_id"));
        t.setPatientId(rs.getInt("patient_id"));
        int docId = rs.getInt("doctor_id");
        if (!rs.wasNull()) t.setDoctorId(docId);
        t.setTestName(rs.getString("test_name"));
        t.setTestResult(rs.getString("test_result"));
        t.setTestDate(rs.getDate("test_date").toLocalDate());
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return t;
    }
}
