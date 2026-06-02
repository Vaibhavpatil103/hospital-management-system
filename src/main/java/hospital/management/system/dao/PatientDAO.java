package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDAO {
    private static final Logger logger = LoggerFactory.getLogger(PatientDAO.class);

    public List<Patient> findAll() {
        return findByCondition("1=1");
    }

    public List<Patient> findAdmitted() {
        return findByCondition("status = 'ADMITTED'");
    }

    private List<Patient> findByCondition(String condition) {
        String sql = "SELECT * FROM patients WHERE " + condition + " ORDER BY admission_time DESC";
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding patients with condition: {}", condition, e);
            throw new DataAccessException("Failed to fetch patients", e);
        }
        return patients;
    }

    public Optional<Patient> findById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding patient by id: {}", patientId, e);
            throw new DataAccessException("Failed to find patient", e);
        }
        return Optional.empty();
    }

    public boolean exists(String idType, String idNumber) {
        String sql = "SELECT 1 FROM patients WHERE id_type = ? AND id_number = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, idType);
            pstmt.setString(2, idNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking patient existence: {} - {}", idType, idNumber, e);
            throw new DataAccessException("Failed to check patient existence", e);
        }
    }

    public void save(Patient patient) {
        String sql = "INSERT INTO patients (id_type, id_number, contact, full_name, gender, age, disease, room_id, deposit, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            setPatientParameters(pstmt, patient);
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    patient.setPatientId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving patient", e);
            throw new DataAccessException("Failed to save patient", e);
        }
    }

    public void update(Patient patient) {
        String sql = "UPDATE patients SET id_type=?, id_number=?, contact=?, full_name=?, gender=?, age=?, disease=?, room_id=?, deposit=?, status=? WHERE patient_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            setPatientParameters(pstmt, patient);
            pstmt.setInt(11, patient.getPatientId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating patient id: {}", patient.getPatientId(), e);
            throw new DataAccessException("Failed to update patient", e);
        }
    }

    public void updateStatus(int patientId, String status) {
        String sql = "UPDATE patients SET status = ? WHERE patient_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, patientId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating patient status id: {}", patientId, e);
            throw new DataAccessException("Failed to update patient status", e);
        }
    }

    private void setPatientParameters(PreparedStatement pstmt, Patient patient) throws SQLException {
        pstmt.setString(1, patient.getIdType());
        pstmt.setString(2, patient.getIdNumber());
        pstmt.setString(3, patient.getContact());
        pstmt.setString(4, patient.getFullName());
        pstmt.setString(5, patient.getGender());
        pstmt.setInt(6, patient.getAge());
        pstmt.setString(7, patient.getDisease());
        
        if (patient.getRoomId() != null) {
            pstmt.setInt(8, patient.getRoomId());
        } else {
            pstmt.setNull(8, Types.INTEGER);
        }
        
        pstmt.setBigDecimal(9, patient.getDeposit());
        pstmt.setString(10, patient.getStatus());
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setIdType(rs.getString("id_type"));
        patient.setIdNumber(rs.getString("id_number"));
        patient.setContact(rs.getString("contact"));
        patient.setFullName(rs.getString("full_name"));
        patient.setGender(rs.getString("gender"));
        patient.setAge(rs.getInt("age"));
        patient.setDisease(rs.getString("disease"));
        
        int roomId = rs.getInt("room_id");
        if (!rs.wasNull()) {
            patient.setRoomId(roomId);
        }
        
        patient.setAdmissionTime(rs.getTimestamp("admission_time").toLocalDateTime());
        patient.setDeposit(rs.getBigDecimal("deposit"));
        patient.setStatus(rs.getString("status"));
        patient.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        patient.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return patient;
    }
}
