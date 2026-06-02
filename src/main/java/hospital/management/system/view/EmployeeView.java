package hospital.management.system.view;

import hospital.management.system.dao.EmployeeDAO;
import hospital.management.system.model.Employee;
import hospital.management.system.model.Role;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.SessionManager;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class EmployeeView extends BaseFrame {

    private final EmployeeDAO employeeDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Employee> currentEmployees;

    public EmployeeView() {
        super("Employee Information", 1100, 700);
        this.employeeDAO = new EmployeeDAO();
        
        setupUI();
        loadData();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout(0, 10));

        // Top Panel for Search and Action Buttons
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh", e -> loadData());
        topPanel.add(refreshBtn);
        
        if (SessionManager.hasRole(Role.ADMIN)) {
            JButton addBtn = UIComponentFactory.createSuccessButton("Add Employee", e -> showEmployeeDialog(null));
            JButton updateBtn = UIComponentFactory.createPrimaryButton("Update Selected", e -> updateSelectedEmployee());
            JButton deleteBtn = UIComponentFactory.createDangerButton("Delete Selected", e -> deleteSelectedEmployee());
            topPanel.add(addBtn);
            topPanel.add(updateBtn);
            topPanel.add(deleteBtn);
        }

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Table setup
        boolean isAdmin = SessionManager.hasRole(Role.ADMIN);
        String[] columns = isAdmin ? 
                new String[]{"ID", "Name", "Age", "Dept ID", "Phone", "Salary", "Email", "Aadhar", "Joined"} :
                new String[]{"ID", "Name", "Age", "Dept ID", "Phone", "Email", "Joined"}; // Hide salary/aadhar for non-admins
                
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = UIComponentFactory.createTableScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton backBtn = UIComponentFactory.createSecondaryButton("Back", e -> dispose());
        buttonPanel.add(backBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        SwingWorker<List<Employee>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Employee> doInBackground() {
                return employeeDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    currentEmployees = get();
                    tableModel.setRowCount(0);
                    boolean isAdmin = SessionManager.hasRole(Role.ADMIN);
                    
                    for (Employee emp : currentEmployees) {
                        if (isAdmin) {
                            tableModel.addRow(new Object[]{
                                emp.getEmpId(), emp.getFullName(), emp.getAge(), 
                                emp.getDepartmentId() != null ? emp.getDepartmentId() : "N/A", 
                                emp.getPhone(), "$" + emp.getSalary(), emp.getEmail(), 
                                emp.getAadharNumber(), emp.getCreatedAt().toLocalDate()
                            });
                        } else {
                            tableModel.addRow(new Object[]{
                                emp.getEmpId(), emp.getFullName(), emp.getAge(), 
                                emp.getDepartmentId() != null ? emp.getDepartmentId() : "N/A", 
                                emp.getPhone(), emp.getEmail(), emp.getCreatedAt().toLocalDate()
                            });
                        }
                    }
                } catch (Exception e) {
                    showError("Failed to load employee data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showEmployeeDialog(Employee employee) {
        JDialog dialog = new JDialog(this, employee == null ? "Add Employee" : "Update Employee", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UIComponentFactory.createTextField();
        JTextField ageField = UIComponentFactory.createTextField();
        JTextField deptField = UIComponentFactory.createTextField(); // Should ideally be combo box
        JTextField phoneField = UIComponentFactory.createTextField();
        JTextField salaryField = UIComponentFactory.createTextField();
        JTextField emailField = UIComponentFactory.createTextField();
        JTextField aadharField = UIComponentFactory.createTextField();

        if (employee != null) {
            nameField.setText(employee.getFullName());
            ageField.setText(String.valueOf(employee.getAge()));
            deptField.setText(employee.getDepartmentId() != null ? String.valueOf(employee.getDepartmentId()) : "");
            phoneField.setText(employee.getPhone());
            salaryField.setText(employee.getSalary().toString());
            emailField.setText(employee.getEmail());
            aadharField.setText(employee.getAadharNumber());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UIComponentFactory.createLabel("Full Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Age:"), gbc);
        gbc.gridx = 1; panel.add(ageField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Dept ID:"), gbc);
        gbc.gridx = 1; panel.add(deptField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Salary:"), gbc);
        gbc.gridx = 1; panel.add(salaryField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Aadhar:"), gbc);
        gbc.gridx = 1; panel.add(aadharField, gbc);

        JButton saveBtn = UIComponentFactory.createPrimaryButton("Save", e -> {
            if (!validateInputs(nameField, ageField, phoneField, salaryField, aadharField, emailField)) return;

            Employee emp = employee != null ? employee : new Employee();
            emp.setFullName(nameField.getText().trim());
            emp.setAge(Integer.parseInt(ageField.getText().trim()));
            if (!deptField.getText().trim().isEmpty()) {
                emp.setDepartmentId(Integer.parseInt(deptField.getText().trim()));
            }
            emp.setPhone(phoneField.getText().trim());
            emp.setSalary(new BigDecimal(salaryField.getText().trim()));
            emp.setEmail(emailField.getText().trim());
            emp.setAadharNumber(aadharField.getText().trim());

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (employee == null) {
                        employeeDAO.save(emp);
                    } else {
                        employeeDAO.update(emp);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadData();
                        showSuccess("Employee saved successfully.");
                    } catch (Exception ex) {
                        showError("Failed to save employee: " + ex.getCause().getMessage());
                    }
                }
            };
            worker.execute();
        });

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(AppTheme.BACKGROUND);
        btnPanel.add(saveBtn);
        btnPanel.add(UIComponentFactory.createSecondaryButton("Cancel", e -> dialog.dispose()));
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateSelectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select an employee to update.");
            return;
        }
        int empId = (int) tableModel.getValueAt(row, 0);
        Employee emp = currentEmployees.stream().filter(e -> e.getEmpId() == empId).findFirst().orElse(null);
        if (emp != null) {
            showEmployeeDialog(emp);
        }
    }

    private void deleteSelectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select an employee to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int empId = (int) tableModel.getValueAt(row, 0);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                employeeDAO.delete(empId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    showSuccess("Employee deleted.");
                    loadData();
                } catch (Exception ex) {
                    showError("Failed to delete employee: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private boolean validateInputs(JTextField name, JTextField age, JTextField phone, JTextField salary, JTextField aadhar, JTextField email) {
        InputValidator.ValidationResult v;
        
        v = InputValidator.validateRequired(name.getText(), "Name");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateEmployeeAge(age.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validatePhone(phone.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validatePositiveAmount(salary.getText(), "Salary");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateAadhar(aadhar.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateEmail(email.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        return true;
    }
}
