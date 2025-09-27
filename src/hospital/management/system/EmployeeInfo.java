package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.regex.Pattern;

import com.formdev.flatlaf.FlatLightLaf;

public class EmployeeInfo extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TABLE_HEADER_COLOR = new Color(0, 112, 192);
    private static final Color TABLE_TEXT_COLOR = new Color(33, 33, 33);

    private JTable table;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{10,15}");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("\\d{12}");

    public EmployeeInfo() {
        initializeUI();
        loadEmployeeData();
        setupButtons();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("HOSPITAL MANAGEMENT SYSTEM - Employee Info");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        add(mainPanel);

        JLabel headerLabel = new JLabel("Employee Information", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setForeground(TABLE_TEXT_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupButtons() {
        JButton addButton = createStyledButton("Add Employee", new Color(39, 174, 96));
        addButton.addActionListener(e -> showEmployeeDialog(false));

        JButton updateButton = createStyledButton("Update Selected", new Color(41, 128, 185));
        updateButton.addActionListener(e -> showEmployeeDialog(true));

        JButton deleteButton = createStyledButton("Delete Selected", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        JButton refreshButton = createStyledButton("Refresh", new Color(155, 89, 182));
        refreshButton.addActionListener(e -> loadEmployeeData());

        JButton backButton = createStyledButton("Back", new Color(128, 128, 128));
        backButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        ((JPanel)getContentPane().getComponent(0)).add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEmployeeData() {
        try (Connection conn = new conn().connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID, Name, Age, Department, Phone_Number, salary, Email, Aadhar_Number FROM EMP_INFO")) {
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            showErrorDialog("Error loading employee data", e);
        }
    }

    private void showEmployeeDialog(boolean isUpdate) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField aadharField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Employee ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Age:")); panel.add(ageField);
        panel.add(new JLabel("Department:")); panel.add(departmentField);
        panel.add(new JLabel("Phone Number:")); panel.add(phoneField);
        panel.add(new JLabel("Salary:")); panel.add(salaryField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Aadhar Number:")); panel.add(aadharField);

        String selectedId = null;

        if (isUpdate) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedId = table.getValueAt(selectedRow, 0).toString();
            idField.setText(selectedId);
            nameField.setText(table.getValueAt(selectedRow, 1).toString());
            ageField.setText(table.getValueAt(selectedRow, 2).toString());
            departmentField.setText(table.getValueAt(selectedRow, 3).toString());
            phoneField.setText(table.getValueAt(selectedRow, 4).toString());
            salaryField.setText(table.getValueAt(selectedRow, 5).toString());
            emailField.setText(table.getValueAt(selectedRow, 6).toString());
            aadharField.setText(table.getValueAt(selectedRow, 7).toString());

            idField.setEditable(false);
        }

        int result = JOptionPane.showConfirmDialog(this, panel,
                isUpdate ? "Update Employee" : "Add Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!validateInputs(idField, nameField, ageField, departmentField,
                    phoneField, salaryField, emailField, aadharField)) {
                return;
            }

            try (Connection conn = new conn().connection) {
                String query;
                if (isUpdate) {
                    query = "UPDATE EMP_INFO SET Name=?, Age=?, Department=?, Phone_Number=?, salary=?, Email=?, Aadhar_Number=? WHERE ID=?";
                } else {
                    query = "INSERT INTO EMP_INFO (ID, Name, Age, Department, Phone_Number, salary, Email, Aadhar_Number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                }

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    if (!isUpdate) {
                        pstmt.setString(1, idField.getText());
                    }

                    pstmt.setString(isUpdate ? 1 : 2, nameField.getText());
                    pstmt.setInt(isUpdate ? 2 : 3, Integer.parseInt(ageField.getText()));
                    pstmt.setString(isUpdate ? 3 : 4, departmentField.getText());
                    pstmt.setString(isUpdate ? 4 : 5, phoneField.getText());
                    pstmt.setDouble(isUpdate ? 5 : 6, Double.parseDouble(salaryField.getText()));
                    pstmt.setString(isUpdate ? 6 : 7, emailField.getText());
                    pstmt.setString(isUpdate ? 7 : 8, aadharField.getText());

                    if (isUpdate) {
                        pstmt.setString(8, selectedId);
                    }

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this,
                                isUpdate ? "Employee updated successfully." : "Employee added successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEmployeeData();
                    }
                }
            } catch (SQLException ex) {
                showErrorDialog("Database error: " + ex.getMessage(), ex);
            } catch (Exception ex) {
                showErrorDialog("Error processing request", ex);
            }
        }
    }

    private boolean validateInputs(JTextField idField, JTextField nameField, JTextField ageField,
                                   JTextField departmentField, JTextField phoneField,
                                   JTextField salaryField, JTextField emailField, JTextField aadharField) {
        try {
            // Validate required fields
            if (idField.getText().trim().isEmpty() && idField.isEditable()) {
                throw new IllegalArgumentException("Employee ID is required");
            }
            if (nameField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (departmentField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Department is required");
            }

            // Validate age
            int age = Integer.parseInt(ageField.getText());
            if (age < 18 || age > 70) {
                throw new IllegalArgumentException("Age must be between 18 and 70");
            }

            // Validate salary
            double salary = Double.parseDouble(salaryField.getText());
            if (salary <= 0) {
                throw new IllegalArgumentException("Salary must be positive");
            }

            // Validate phone
            if (!PHONE_PATTERN.matcher(phoneField.getText()).matches()) {
                throw new IllegalArgumentException("Phone number must be 10-15 digits");
            }

            // Validate email
            if (!EMAIL_PATTERN.matcher(emailField.getText()).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }

            // Validate aadhar
            if (!AADHAR_PATTERN.matcher(aadharField.getText()).matches()) {
                throw new IllegalArgumentException("Aadhar must be 12 digits");
            }

            return true;
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter valid numbers for age and salary", e);
            return false;
        } catch (IllegalArgumentException e) {
            showErrorDialog(e.getMessage(), e);
            return false;
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = new conn().connection;
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM EMP_INFO WHERE ID=?")) {

                String employeeId = table.getValueAt(selectedRow, 0).toString();
                pstmt.setString(1, employeeId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Employee deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEmployeeData();
                }
            } catch (SQLException ex) {
                showErrorDialog("Database error while deleting employee: " + ex.getMessage(), ex);
            }
        }
    }

    private void showErrorDialog(String message, Exception ex) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new EmployeeInfo());
    }
}