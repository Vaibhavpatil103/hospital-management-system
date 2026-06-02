package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class BillDAO {
    private static final Logger logger = LoggerFactory.getLogger(BillDAO.class);

    public Optional<Bill> findByPatientId(int patientId) {
        String sql = "SELECT * FROM bills WHERE patient_id = ? ORDER BY bill_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding bill by patient id: {}", patientId, e);
            throw new DataAccessException("Failed to find bill", e);
        }
        return Optional.empty();
    }

    public void save(Bill bill) {
        String sql = "INSERT INTO bills (patient_id, room_charges, doctor_fees, other_charges, total_amount, deposit_paid, balance_due, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setInt(1, bill.getPatientId());
            pstmt.setBigDecimal(2, bill.getRoomCharges());
            pstmt.setBigDecimal(3, bill.getDoctorFees());
            pstmt.setBigDecimal(4, bill.getOtherCharges());
            pstmt.setBigDecimal(5, bill.getTotalAmount());
            pstmt.setBigDecimal(6, bill.getDepositPaid());
            pstmt.setBigDecimal(7, bill.getBalanceDue());
            pstmt.setString(8, bill.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bill.setBillId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving bill", e);
            throw new DataAccessException("Failed to save bill", e);
        }
    }

    public void updateStatus(int billId, String status) {
        String sql = "UPDATE bills SET status = ? WHERE bill_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, billId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating bill status id: {}", billId, e);
            throw new DataAccessException("Failed to update bill status", e);
        }
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setPatientId(rs.getInt("patient_id"));
        bill.setRoomCharges(rs.getBigDecimal("room_charges"));
        bill.setDoctorFees(rs.getBigDecimal("doctor_fees"));
        bill.setOtherCharges(rs.getBigDecimal("other_charges"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setDepositPaid(rs.getBigDecimal("deposit_paid"));
        bill.setBalanceDue(rs.getBigDecimal("balance_due"));
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        bill.setStatus(rs.getString("status"));
        return bill;
    }
}
