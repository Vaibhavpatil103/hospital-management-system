package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(InventoryDAO.class);

    public List<InventoryItem> findAll() {
        String sql = "SELECT * FROM inventory ORDER BY item_name";
        List<InventoryItem> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding inventory", e);
            throw new DataAccessException("Failed to fetch inventory", e);
        }
        return list;
    }

    public void save(InventoryItem item) {
        String sql = "INSERT INTO inventory (item_name, category, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setBigDecimal(4, item.getUnitPrice());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) item.setItemId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Error saving inventory", e);
            throw new DataAccessException("Failed to save inventory item", e);
        }
    }

    public void update(InventoryItem item) {
        String sql = "UPDATE inventory SET item_name=?, category=?, quantity=?, unit_price=? WHERE item_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setBigDecimal(4, item.getUnitPrice());
            pstmt.setInt(5, item.getItemId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating inventory", e);
            throw new DataAccessException("Failed to update inventory item", e);
        }
    }

    private InventoryItem mapResultSet(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return item;
    }
}
