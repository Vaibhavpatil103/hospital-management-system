package hospital.management.system.view;

import hospital.management.system.model.Appointment;
import hospital.management.system.model.Doctor;
import hospital.management.system.service.AppointmentService;
import hospital.management.system.service.DoctorService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppointmentView extends JPanel {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Appointment> currentAppointments;

    public AppointmentView() {
        this.appointmentService = new AppointmentService();
        this.doctorService = new DoctorService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh", e -> loadData());
        JButton addBtn = UIComponentFactory.createSuccessButton("Book Appointment", e -> showAppointmentDialog());
        JButton completeBtn = UIComponentFactory.createPrimaryButton("Mark Completed", e -> markSelectedCompleted());
        JButton cancelBtn = UIComponentFactory.createDangerButton("Cancel Appointment", e -> cancelSelected());

        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(completeBtn);
        topPanel.add(cancelBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Patient Name", "Phone", "Doctor ID", "Date", "Time", "Status"};
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
        SwingWorker<List<Appointment>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Appointment> doInBackground() {
                return appointmentService.getAllAppointments();
            }

            @Override
            protected void done() {
                try {
                    currentAppointments = get();
                    tableModel.setRowCount(0);
                    for (Appointment appt : currentAppointments) {
                        tableModel.addRow(new Object[]{
                            appt.getAppointmentId(), appt.getPatientName(), appt.getPatientPhone(),
                            appt.getDoctorId(), appt.getAppointmentDate(), appt.getAppointmentTime(),
                            appt.getStatus().name()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AppointmentView.this, "Failed to load appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField patientNameField = UIComponentFactory.createTextField();
        JTextField patientPhoneField = UIComponentFactory.createTextField();
        JTextField dateField = UIComponentFactory.createTextField(); // YYYY-MM-DD
        dateField.setText(LocalDate.now().toString());
        JTextField timeField = UIComponentFactory.createTextField(); // HH:MM
        timeField.setText("10:00");

        JComboBox<Doctor> doctorBox = new JComboBox<>();
        try {
            List<Doctor> doctors = doctorService.getActiveDoctors();
            for (Doctor d : doctors) doctorBox.addItem(d);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load doctors");
            return;
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UIComponentFactory.createLabel("Patient Name:"), gbc);
        gbc.gridx = 1; panel.add(patientNameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(patientPhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Doctor:"), gbc);
        gbc.gridx = 1; panel.add(doctorBox, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; panel.add(timeField, gbc);

        JButton saveBtn = UIComponentFactory.createPrimaryButton("Book", e -> {
            Appointment appt = new Appointment();
            appt.setPatientName(patientNameField.getText().trim());
            appt.setPatientPhone(patientPhoneField.getText().trim());
            Doctor selectedDoc = (Doctor) doctorBox.getSelectedItem();
            if (selectedDoc == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a doctor.");
                return;
            }
            appt.setDoctorId(selectedDoc.getDoctorId());
            
            try {
                appt.setAppointmentDate(LocalDate.parse(dateField.getText().trim()));
                appt.setAppointmentTime(LocalTime.parse(timeField.getText().trim()));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format.");
                return;
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    appointmentService.scheduleAppointment(appt);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadData();
                        JOptionPane.showMessageDialog(AppointmentView.this, "Appointment booked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AppointmentView.this, "Failed to book: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void markSelectedCompleted() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int apptId = (int) tableModel.getValueAt(row, 0);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                appointmentService.markAppointmentCompleted(apptId);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AppointmentView.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int apptId = (int) tableModel.getValueAt(row, 0);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                appointmentService.cancelAppointment(apptId);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AppointmentView.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
