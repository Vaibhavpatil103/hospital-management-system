package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Ambulance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmbulanceDAO {
    private static final Logger logger = LoggerFactory.getLogger(AmbulanceDAO.class);

    public List<Ambulance> findAll() {
        String sql = "SELECT * FROM ambulances";
        List<Ambulance> ambulances = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                ambulances.add(mapResultSetToAmbulance(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all ambulances", e);
            throw new DataAccessException("Failed to fetch ambulances", e);
        }
        return ambulances;
    }

    public void save(Ambulance ambulance) {
        String sql = "INSERT INTO ambulances (driver_name, contact, vehicle_name, is_available, location) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, ambulance.getDriverName());
            pstmt.setString(2, ambulance.getContact());
            pstmt.setString(3, ambulance.getVehicleName());
            pstmt.setBoolean(4, ambulance.isAvailable());
            pstmt.setString(5, ambulance.getLocation());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ambulance.setAmbulanceId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving ambulance", e);
            throw new DataAccessException("Failed to save ambulance", e);
        }
    }

    public void update(Ambulance ambulance) {
        String sql = "UPDATE ambulances SET driver_name=?, contact=?, vehicle_name=?, is_available=?, location=? WHERE ambulance_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, ambulance.getDriverName());
            pstmt.setString(2, ambulance.getContact());
            pstmt.setString(3, ambulance.getVehicleName());
            pstmt.setBoolean(4, ambulance.isAvailable());
            pstmt.setString(5, ambulance.getLocation());
            pstmt.setInt(6, ambulance.getAmbulanceId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating ambulance id: {}", ambulance.getAmbulanceId(), e);
            throw new DataAccessException("Failed to update ambulance", e);
        }
    }

    public void delete(int ambulanceId) {
        String sql = "DELETE FROM ambulances WHERE ambulance_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, ambulanceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting ambulance id: {}", ambulanceId, e);
            throw new DataAccessException("Failed to delete ambulance", e);
        }
    }

    private Ambulance mapResultSetToAmbulance(ResultSet rs) throws SQLException {
        Ambulance ambulance = new Ambulance();
        ambulance.setAmbulanceId(rs.getInt("ambulance_id"));
        ambulance.setDriverName(rs.getString("driver_name"));
        ambulance.setContact(rs.getString("contact"));
        ambulance.setVehicleName(rs.getString("vehicle_name"));
        ambulance.setAvailable(rs.getBoolean("is_available"));
        ambulance.setLocation(rs.getString("location"));
        ambulance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return ambulance;
    }
}
