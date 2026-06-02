package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentDAO.class);

    public List<Department> findAll() {
        String sql = "SELECT * FROM departments";
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all departments", e);
            throw new DataAccessException("Failed to fetch departments", e);
        }
        return departments;
    }

    public Optional<Department> findById(int deptId) {
        String sql = "SELECT * FROM departments WHERE dept_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, deptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by id: {}", deptId, e);
            throw new DataAccessException("Failed to find department", e);
        }
        return Optional.empty();
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDeptId(rs.getInt("dept_id"));
        dept.setDeptName(rs.getString("dept_name"));
        dept.setHeadDoctor(rs.getString("head_doctor"));
        dept.setPhone(rs.getString("phone"));
        dept.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return dept;
    }
}
