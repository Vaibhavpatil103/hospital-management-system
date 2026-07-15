package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Prescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionDAO.class);

    public List<Prescription> findByPatientId(int patientId) {
        String sql = "SELECT * FROM prescriptions WHERE patient_id = ? ORDER BY prescribed_date DESC";
        List<Prescription> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding prescriptions", e);
            throw new DataAccessException("Failed to fetch prescriptions", e);
        }
        return list;
    }

    public void save(Prescription p) {
        String sql = "INSERT INTO prescriptions (patient_id, doctor_id, medicine_name, dosage, duration, notes, prescribed_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, p.getPatientId());
            pstmt.setInt(2, p.getDoctorId());
            pstmt.setString(3, p.getMedicineName());
            pstmt.setString(4, p.getDosage());
            pstmt.setString(5, p.getDuration());
            pstmt.setString(6, p.getNotes());
            pstmt.setDate(7, Date.valueOf(p.getPrescribedDate()));
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) p.setPrescriptionId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Error saving prescription", e);
            throw new DataAccessException("Failed to save prescription", e);
        }
    }

    private Prescription mapResultSet(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setPrescriptionId(rs.getInt("prescription_id"));
        p.setPatientId(rs.getInt("patient_id"));
        p.setDoctorId(rs.getInt("doctor_id"));
        p.setMedicineName(rs.getString("medicine_name"));
        p.setDosage(rs.getString("dosage"));
        p.setDuration(rs.getString("duration"));
        p.setNotes(rs.getString("notes"));
        p.setPrescribedDate(rs.getDate("prescribed_date").toLocalDate());
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return p;
    }
}
