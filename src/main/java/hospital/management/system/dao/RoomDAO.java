package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoomDAO.class);

    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all rooms", e);
            throw new DataAccessException("Failed to fetch rooms", e);
        }
        return rooms;
    }

    public List<Room> findAvailable() {
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding available rooms", e);
            throw new DataAccessException("Failed to fetch available rooms", e);
        }
        return rooms;
    }

    public List<Room> findAvailableByType(String roomType) {
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE AND room_type = ?";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, roomType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding available rooms by type: {}", roomType, e);
            throw new DataAccessException("Failed to fetch available rooms by type", e);
        }
        return rooms;
    }

    public Optional<Room> findById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding room by id: {}", roomId, e);
            throw new DataAccessException("Failed to find room", e);
        }
        return Optional.empty();
    }

    public void updateAvailability(int roomId, boolean isAvailable) {
        String sql = "UPDATE rooms SET is_available = ? WHERE room_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating room availability for id: {}", roomId, e);
            throw new DataAccessException("Failed to update room availability", e);
        }
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPricePerDay(rs.getBigDecimal("price_per_day"));
        room.setAvailable(rs.getBoolean("is_available"));
        return room;
    }
}
