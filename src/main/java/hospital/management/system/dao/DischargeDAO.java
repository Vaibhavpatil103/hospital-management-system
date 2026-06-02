package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.DischargeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DischargeDAO {
    private static final Logger logger = LoggerFactory.getLogger(DischargeDAO.class);

    public List<DischargeRecord> findAll() {
        String sql = "SELECT * FROM discharge_records ORDER BY check_out_time DESC";
        List<DischargeRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                records.add(mapResultSetToDischargeRecord(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all discharge records", e);
            throw new DataAccessException("Failed to fetch discharge records", e);
        }
        return records;
    }

    public void save(DischargeRecord record) {
        String sql = "INSERT INTO discharge_records (patient_id, room_id, check_in_time, check_out_time, deposit, total_bill, discharged_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setInt(1, record.getPatientId());
            
            if (record.getRoomId() != null) {
                pstmt.setInt(2, record.getRoomId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setTimestamp(3, Timestamp.valueOf(record.getCheckInTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(record.getCheckOutTime()));
            pstmt.setBigDecimal(5, record.getDeposit());
            pstmt.setBigDecimal(6, record.getTotalBill());
            
            if (record.getDischargedBy() != null) {
                pstmt.setInt(7, record.getDischargedBy());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    record.setDischargeId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving discharge record", e);
            throw new DataAccessException("Failed to save discharge record", e);
        }
    }

    private DischargeRecord mapResultSetToDischargeRecord(ResultSet rs) throws SQLException {
        DischargeRecord record = new DischargeRecord();
        record.setDischargeId(rs.getInt("discharge_id"));
        record.setPatientId(rs.getInt("patient_id"));
        
        int roomId = rs.getInt("room_id");
        if (!rs.wasNull()) {
            record.setRoomId(roomId);
        }
        
        record.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
        record.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
        record.setDeposit(rs.getBigDecimal("deposit"));
        record.setTotalBill(rs.getBigDecimal("total_bill"));
        
        int dischargedBy = rs.getInt("discharged_by");
        if (!rs.wasNull()) {
            record.setDischargedBy(dischargedBy);
        }
        
        record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return record;
    }
}
