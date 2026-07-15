package hospital.management.system.view;

import hospital.management.system.model.Doctor;
import hospital.management.system.service.DoctorService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DoctorView extends JPanel {
    private final DoctorService doctorService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Doctor> currentDoctors;

    public DoctorView() {
        this.doctorService = new DoctorService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh", e -> loadData());
        JButton addBtn = UIComponentFactory.createSuccessButton("Add Doctor", e -> showDoctorDialog(null));
        JButton updateBtn = UIComponentFactory.createPrimaryButton("Update Selected", e -> updateSelectedDoctor());

        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Specialization", "Dept ID", "Phone", "Email", "Fee", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = UIComponentFactory.createTableScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Doctor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Doctor> doInBackground() {
                return doctorService.getAllDoctors();
            }

            @Override
            protected void done() {
                try {
                    currentDoctors = get();
                    tableModel.setRowCount(0);
                    for (Doctor doc : currentDoctors) {
                        tableModel.addRow(new Object[]{
                            doc.getDoctorId(), doc.getFullName(), doc.getSpecialization(),
                            doc.getDepartmentId() != null ? doc.getDepartmentId() : "N/A",
                            doc.getPhone(), doc.getEmail(), "$" + doc.getConsultationFee(), doc.isActive() ? "Yes" : "No"
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DoctorView.this, "Failed to load doctors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showDoctorDialog(Doctor doctor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), doctor == null ? "Add Doctor" : "Update Doctor", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UIComponentFactory.createTextField();
        JTextField specField = UIComponentFactory.createTextField();
        JTextField deptField = UIComponentFactory.createTextField();
        JTextField phoneField = UIComponentFactory.createTextField();
        JTextField emailField = UIComponentFactory.createTextField();
        JTextField feeField = UIComponentFactory.createTextField();
        JCheckBox activeCheck = new JCheckBox("Is Active");
        activeCheck.setBackground(AppTheme.BACKGROUND);
        activeCheck.setSelected(true);

        if (doctor != null) {
            nameField.setText(doctor.getFullName());
            specField.setText(doctor.getSpecialization());
            deptField.setText(doctor.getDepartmentId() != null ? String.valueOf(doctor.getDepartmentId()) : "");
            phoneField.setText(doctor.getPhone());
            emailField.setText(doctor.getEmail());
            feeField.setText(doctor.getConsultationFee() != null ? doctor.getConsultationFee().toString() : "0.00");
            activeCheck.setSelected(doctor.isActive());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UIComponentFactory.createLabel("Full Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Specialization:"), gbc);
        gbc.gridx = 1; panel.add(specField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Dept ID:"), gbc);
        gbc.gridx = 1; panel.add(deptField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Fee:"), gbc);
        gbc.gridx = 1; panel.add(feeField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Status:"), gbc);
        gbc.gridx = 1; panel.add(activeCheck, gbc);

        JButton saveBtn = UIComponentFactory.createPrimaryButton("Save", e -> {
            Doctor doc = doctor != null ? doctor : new Doctor();
            doc.setFullName(nameField.getText().trim());
            doc.setSpecialization(specField.getText().trim());
            
            try {
                if (!deptField.getText().trim().isEmpty()) {
                    doc.setDepartmentId(Integer.parseInt(deptField.getText().trim()));
                } else {
                    doc.setDepartmentId(null);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Department ID");
                return;
            }
            
            doc.setPhone(phoneField.getText().trim());
            doc.setEmail(emailField.getText().trim());
            try {
                doc.setConsultationFee(new BigDecimal(feeField.getText().trim()));
            } catch (Exception ex) {
                doc.setConsultationFee(BigDecimal.ZERO);
            }
            doc.setActive(activeCheck.isSelected());

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (doctor == null) {
                        doctorService.addDoctor(doc);
                    } else {
                        doctorService.updateDoctor(doc);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadData();
                        JOptionPane.showMessageDialog(DoctorView.this, "Doctor saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DoctorView.this, "Failed to save doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void updateSelectedDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int docId = (int) tableModel.getValueAt(row, 0);
        Doctor doc = currentDoctors.stream().filter(d -> d.getDoctorId() == docId).findFirst().orElse(null);
        if (doc != null) {
            showDoctorDialog(doc);
        }
    }
}
