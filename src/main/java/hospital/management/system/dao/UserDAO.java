package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Role;
import hospital.management.system.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
            throw new DataAccessException("Failed to find user", e);
        }
        return Optional.empty();
    }

    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating last login for user ID: {}", userId, e);
            throw new DataAccessException("Failed to update last login", e);
        }
    }

    public void updatePasswordHash(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newHash);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating password hash for user ID: {}", userId, e);
            throw new DataAccessException("Failed to update password hash", e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        return user;
    }
}
