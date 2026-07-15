package hospital.management.system.view;

import hospital.management.system.service.AmbulanceService;
import hospital.management.system.model.Ambulance;
import hospital.management.system.model.Role;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.SessionManager;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AmbulanceView extends JPanel {

    private final AmbulanceService ambulanceService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Ambulance> currentAmbulances;

    public AmbulanceView() {
        this.ambulanceService = new AmbulanceService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // Top Panel
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh", e -> loadData());
        topPanel.add(refreshBtn);
        
        if (SessionManager.hasRole(Role.ADMIN) || SessionManager.hasRole(Role.RECEPTIONIST)) {
            JButton addBtn = UIComponentFactory.createSuccessButton("Add Ambulance", e -> showAmbulanceDialog(null));
            JButton updateBtn = UIComponentFactory.createPrimaryButton("Update Selected", e -> updateSelected());
            JButton deleteBtn = UIComponentFactory.createDangerButton("Delete Selected", e -> deleteSelected());
            
            topPanel.add(addBtn);
            topPanel.add(updateBtn);
            if (SessionManager.hasRole(Role.ADMIN)) {
                topPanel.add(deleteBtn);
            }
        }
        
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Driver Name", "Contact", "Vehicle Name", "Status", "Location", "Added"};
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
        SwingWorker<List<Ambulance>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Ambulance> doInBackground() {
                return ambulanceService.getAllAmbulances();
            }

            @Override
            protected void done() {
                try {
                    currentAmbulances = get();
                    tableModel.setRowCount(0);
                    for (Ambulance a : currentAmbulances) {
                        tableModel.addRow(new Object[]{
                            a.getAmbulanceId(), a.getDriverName(), a.getContact(),
                            a.getVehicleName(), a.isAvailable() ? "Available" : "Busy",
                            a.getLocation(), a.getCreatedAt().toLocalDate()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AmbulanceView.this, "Failed to load ambulances: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAmbulanceDialog(Ambulance ambulance) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), ambulance == null ? "Add Ambulance" : "Update Ambulance", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField driverField = UIComponentFactory.createTextField();
        JTextField contactField = UIComponentFactory.createTextField();
        JTextField vehicleField = UIComponentFactory.createTextField();
        JComboBox<String> statusCombo = UIComponentFactory.createComboBox(new String[]{"Available", "Busy"});
        JTextField locationField = UIComponentFactory.createTextField();

        if (ambulance != null) {
            driverField.setText(ambulance.getDriverName());
            contactField.setText(ambulance.getContact());
            vehicleField.setText(ambulance.getVehicleName());
            statusCombo.setSelectedItem(ambulance.isAvailable() ? "Available" : "Busy");
            locationField.setText(ambulance.getLocation());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UIComponentFactory.createLabel("Driver Name:"), gbc);
        gbc.gridx = 1; panel.add(driverField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Contact:"), gbc);
        gbc.gridx = 1; panel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Vehicle Name:"), gbc);
        gbc.gridx = 1; panel.add(vehicleField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Status:"), gbc);
        gbc.gridx = 1; panel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Location:"), gbc);
        gbc.gridx = 1; panel.add(locationField, gbc);

        JButton saveBtn = UIComponentFactory.createPrimaryButton("Save", e -> {
            Ambulance amb = ambulance != null ? ambulance : new Ambulance();
            amb.setDriverName(driverField.getText().trim());
            amb.setContact(contactField.getText().trim());
            amb.setVehicleName(vehicleField.getText().trim());
            amb.setAvailable("Available".equals(statusCombo.getSelectedItem()));
            amb.setLocation(locationField.getText().trim());

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (ambulance == null) {
                        ambulanceService.addAmbulance(amb);
                    } else {
                        ambulanceService.updateAmbulance(amb);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadData();
                        JOptionPane.showMessageDialog(AmbulanceView.this, "Ambulance saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AmbulanceView.this, "Failed to save ambulance: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
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

    // Manual validation removed; handled by AmbulanceService

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an ambulance to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Ambulance amb = currentAmbulances.stream().filter(a -> a.getAmbulanceId() == id).findFirst().orElse(null);
        if (amb != null) {
            showAmbulanceDialog(amb);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an ambulance to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ambulance?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    ambulanceService.deleteAmbulance(id);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        loadData();
                        JOptionPane.showMessageDialog(AmbulanceView.this, "Ambulance deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AmbulanceView.this, "Failed to delete: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
