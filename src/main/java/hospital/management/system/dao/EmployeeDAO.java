package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDAO.class);

    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all employees", e);
            throw new DataAccessException("Failed to fetch employees", e);
        }
        return employees;
    }

    public Optional<Employee> findById(int empId) {
        String sql = "SELECT * FROM employees WHERE emp_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by id: {}", empId, e);
            throw new DataAccessException("Failed to find employee", e);
        }
        return Optional.empty();
    }

    public void save(Employee emp) {
        String sql = "INSERT INTO employees (full_name, age, department_id, phone, salary, email, aadhar_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, emp.getFullName());
            pstmt.setInt(2, emp.getAge());
            if (emp.getDepartmentId() != null) {
                pstmt.setInt(3, emp.getDepartmentId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, emp.getPhone());
            pstmt.setBigDecimal(5, emp.getSalary());
            pstmt.setString(6, emp.getEmail());
            pstmt.setString(7, emp.getAadharNumber());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    emp.setEmpId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving employee", e);
            throw new DataAccessException("Failed to save employee", e);
        }
    }

    public void update(Employee emp) {
        String sql = "UPDATE employees SET full_name=?, age=?, department_id=?, phone=?, salary=?, email=?, aadhar_number=? WHERE emp_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, emp.getFullName());
            pstmt.setInt(2, emp.getAge());
            if (emp.getDepartmentId() != null) {
                pstmt.setInt(3, emp.getDepartmentId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, emp.getPhone());
            pstmt.setBigDecimal(5, emp.getSalary());
            pstmt.setString(6, emp.getEmail());
            pstmt.setString(7, emp.getAadharNumber());
            pstmt.setInt(8, emp.getEmpId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating employee id: {}", emp.getEmpId(), e);
            throw new DataAccessException("Failed to update employee", e);
        }
    }

    public void delete(int empId) {
        String sql = "DELETE FROM employees WHERE emp_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, empId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting employee id: {}", empId, e);
            throw new DataAccessException("Failed to delete employee", e);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpId(rs.getInt("emp_id"));
        emp.setFullName(rs.getString("full_name"));
        emp.setAge(rs.getInt("age"));
        
        int deptId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            emp.setDepartmentId(deptId);
        }
        
        emp.setPhone(rs.getString("phone"));
        emp.setSalary(rs.getBigDecimal("salary"));
        emp.setEmail(rs.getString("email"));
        emp.setAadharNumber(rs.getString("aadhar_number"));
        emp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return emp;
    }
}
