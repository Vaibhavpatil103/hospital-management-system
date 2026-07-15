package hospital.management.system.service;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.dao.*;
import hospital.management.system.model.Bill;
import hospital.management.system.model.DischargeRecord;
import hospital.management.system.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class DischargeService {
    private static final Logger logger = LoggerFactory.getLogger(DischargeService.class);

    private final DischargeDAO dischargeDAO;
    private final PatientDAO patientDAO;
    private final RoomDAO roomDAO;
    private final AuditService auditService;

    public DischargeService() {
        this.dischargeDAO = new DischargeDAO();
        this.patientDAO = new PatientDAO();
        this.roomDAO = new RoomDAO();
        this.auditService = new AuditService();
    }

    /**
     * Discharges a patient transactionally.
     * 1. Inserts into discharge_records
     * 2. Updates patient status to DISCHARGED
     * 3. Sets room to Available
     */
    public void dischargePatient(Patient patient, Bill bill, int dischargedByUserId) {
        logger.info("Starting discharge process for patient ID: {}", patient.getPatientId());
        
        String insertDischargeSql = "INSERT INTO discharge_records (patient_id, room_id, check_in_time, deposit, total_bill, discharged_by) VALUES (?, ?, ?, ?, ?, ?)";
        String updatePatientSql = "UPDATE patients SET status = 'DISCHARGED' WHERE patient_id = ?";
        String updateRoomSql = "UPDATE rooms SET is_available = TRUE WHERE room_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert discharge record
            try (PreparedStatement pstmt = conn.prepareStatement(insertDischargeSql)) {
                pstmt.setInt(1, patient.getPatientId());
                if (patient.getRoomId() != null) {
                    pstmt.setInt(2, patient.getRoomId());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                pstmt.setTimestamp(3, Timestamp.valueOf(patient.getAdmissionTime()));
                pstmt.setBigDecimal(4, patient.getDeposit());
                pstmt.setBigDecimal(5, bill != null ? bill.getTotalAmount() : patient.getDeposit());
                pstmt.setInt(6, dischargedByUserId);
                
                pstmt.executeUpdate();
            }

            // 2. Update patient status (Soft Delete)
            try (PreparedStatement pstmt = conn.prepareStatement(updatePatientSql)) {
                pstmt.setInt(1, patient.getPatientId());
                pstmt.executeUpdate();
            }

            // 3. Free up room
            if (patient.getRoomId() != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(updateRoomSql)) {
                    pstmt.setInt(1, patient.getRoomId());
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            
            // Log the event
            auditService.logEvent("DISCHARGE", "Discharged patient: " + patient.getFullName(), dischargedByUserId);
            
            logger.info("Patient {} discharged successfully.", patient.getPatientId());

        } catch (SQLException e) {
            logger.error("Transaction failed during discharge for patient {}", patient.getPatientId(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Transaction rolled back");
                } catch (SQLException ex) {
                    logger.error("Failed to rollback transaction", ex);
                }
            }
            throw new DataAccessException("Discharge transaction failed", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Failed to close connection after transaction", e);
                }
            }
        }
    }
}
