package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Appointment;
import hospital.management.system.model.AppointmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentDAO.class);

    public List<Appointment> findAll() {
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";
        List<Appointment> list = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding appointments", e);
            throw new DataAccessException("Failed to fetch appointments", e);
        }
        return list;
    }

    public List<Appointment> findByPatientId(int patientId) {
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, appointment_time DESC";
        List<Appointment> list = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding appointments for patient", e);
            throw new DataAccessException("Failed to fetch appointments", e);
        }
        return list;
    }

    public List<Appointment> findTodayAppointments() {
        String sql = "SELECT * FROM appointments WHERE appointment_date = CURDATE() AND status = 'SCHEDULED' ORDER BY appointment_time ASC";
        List<Appointment> list = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding today's appointments", e);
            throw new DataAccessException("Failed to fetch today's appointments", e);
        }
        return list;
    }

    public void save(Appointment appt) {
        String sql = "INSERT INTO appointments (patient_id, patient_name, patient_phone, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            if (appt.getPatientId() != null) {
                pstmt.setInt(1, appt.getPatientId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, appt.getPatientName());
            pstmt.setString(3, appt.getPatientPhone());
            pstmt.setInt(4, appt.getDoctorId());
            pstmt.setDate(5, Date.valueOf(appt.getAppointmentDate()));
            pstmt.setTime(6, Time.valueOf(appt.getAppointmentTime()));
            pstmt.setString(7, appt.getStatus().name());
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    appt.setAppointmentId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving appointment", e);
            throw new DataAccessException("Failed to save appointment", e);
        }
    }

    public void updateStatus(int appointmentId, AppointmentStatus status) {
        String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status.name());
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating appointment status", e);
            throw new DataAccessException("Failed to update appointment status", e);
        }
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        
        int patientId = rs.getInt("patient_id");
        if (!rs.wasNull()) a.setPatientId(patientId);
        
        a.setPatientName(rs.getString("patient_name"));
        a.setPatientPhone(rs.getString("patient_phone"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        a.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return a;
    }
}
