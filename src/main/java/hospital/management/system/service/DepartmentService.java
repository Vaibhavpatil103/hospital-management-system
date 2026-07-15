package hospital.management.system.service;

import hospital.management.system.dao.DepartmentDAO;
import hospital.management.system.model.Department;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.InputValidator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service layer for Department business logic.
 */
public class DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);
    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    public void addDepartment(Department department) {
        logger.info("Adding new department: {}", department.getDeptName());
        validateDepartment(department);
        departmentDAO.save(department);
        logger.info("Department added successfully with ID: {}", department.getDeptId());
    }

    public void updateDepartment(Department department) {
        logger.info("Updating department ID: {}", department.getDeptId());
        validateDepartment(department);
        departmentDAO.update(department);
        logger.info("Department updated successfully: {}", department.getDeptId());
    }

    public void deleteDepartment(int deptId) {
        logger.info("Deleting department ID: {}", deptId);
        departmentDAO.delete(deptId);
        logger.info("Department deleted successfully: {}", deptId);
    }

    private void validateDepartment(Department department) {
        ValidationResult v;

        v = InputValidator.validateRequired(department.getDeptName(), "Department Name");
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());
    }
}
