package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private static final Logger logger = LoggerFactory.getLogger(DoctorDAO.class);

    public List<Doctor> findAll() {
        String sql = "SELECT * FROM doctors ORDER BY full_name";
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all doctors", e);
            throw new DataAccessException("Failed to fetch doctors", e);
        }
        return doctors;
    }

    public List<Doctor> findActiveDoctors() {
        String sql = "SELECT * FROM doctors WHERE is_active = TRUE ORDER BY full_name";
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active doctors", e);
            throw new DataAccessException("Failed to fetch active doctors", e);
        }
        return doctors;
    }

    public java.util.Optional<Doctor> findById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return java.util.Optional.of(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding doctor by id", e);
            throw new DataAccessException("Failed to fetch doctor", e);
        }
        return java.util.Optional.empty();
    }

    public void save(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, full_name, specialization, department_id, phone, email, consultation_fee, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            if (doctor.getUserId() != null) {
                pstmt.setInt(1, doctor.getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, doctor.getFullName());
            pstmt.setString(3, doctor.getSpecialization());
            
            if (doctor.getDepartmentId() != null) {
                pstmt.setInt(4, doctor.getDepartmentId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setString(5, doctor.getPhone());
            pstmt.setString(6, doctor.getEmail());
            pstmt.setBigDecimal(7, doctor.getConsultationFee());
            pstmt.setBoolean(8, doctor.isActive());
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    doctor.setDoctorId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving doctor", e);
            throw new DataAccessException("Failed to save doctor", e);
        }
    }

    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET full_name=?, specialization=?, department_id=?, phone=?, email=?, consultation_fee=?, is_active=? WHERE doctor_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, doctor.getFullName());
            pstmt.setString(2, doctor.getSpecialization());
            
            if (doctor.getDepartmentId() != null) {
                pstmt.setInt(3, doctor.getDepartmentId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setString(4, doctor.getPhone());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setBigDecimal(6, doctor.getConsultationFee());
            pstmt.setBoolean(7, doctor.isActive());
            pstmt.setInt(8, doctor.getDoctorId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating doctor", e);
            throw new DataAccessException("Failed to update doctor", e);
        }
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorId(rs.getInt("doctor_id"));
        
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) d.setUserId(userId);
        
        d.setFullName(rs.getString("full_name"));
        d.setSpecialization(rs.getString("specialization"));
        
        int deptId = rs.getInt("department_id");
        if (!rs.wasNull()) d.setDepartmentId(deptId);
        
        d.setPhone(rs.getString("phone"));
        d.setEmail(rs.getString("email"));
        d.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return d;
    }
}
