package hospital.management.system.service;

import hospital.management.system.dao.EmployeeDAO;
import hospital.management.system.model.Employee;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.InputValidator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Employee business logic.
 * Enforces validation and logging before delegating to the DAO.
 */
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }

    public Optional<Employee> getEmployeeById(int empId) {
        return employeeDAO.findById(empId);
    }

    /**
     * Validates and saves a new employee.
     */
    public void addEmployee(Employee employee) {
        logger.info("Adding new employee: {}", employee.getFullName());
        validateEmployee(employee);
        employeeDAO.save(employee);
        logger.info("Employee added successfully with ID: {}", employee.getEmpId());
    }

    /**
     * Validates and updates an existing employee.
     */
    public void updateEmployee(Employee employee) {
        logger.info("Updating employee ID: {}", employee.getEmpId());
        validateEmployee(employee);
        employeeDAO.update(employee);
        logger.info("Employee updated successfully: {}", employee.getEmpId());
    }

    /**
     * Deletes an employee by ID.
     */
    public void deleteEmployee(int empId) {
        logger.info("Deleting employee ID: {}", empId);
        employeeDAO.delete(empId);
        logger.info("Employee deleted successfully: {}", empId);
    }

    /**
     * Validates employee data before persistence.
     * @throws IllegalStateException if validation fails
     */
    private void validateEmployee(Employee employee) {
        ValidationResult v;

        v = InputValidator.validateRequired(employee.getFullName(), "Full Name");
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validateEmployeeAge(String.valueOf(employee.getAge()));
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validatePhone(employee.getPhone());
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        if (employee.getSalary() == null || employee.getSalary().signum() < 0) {
            throw new IllegalStateException("Salary must be a positive value.");
        }

        v = InputValidator.validateAadhar(employee.getAadharNumber());
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validateEmail(employee.getEmail());
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());
    }
}
