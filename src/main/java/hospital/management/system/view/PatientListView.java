package hospital.management.system.view;

import hospital.management.system.model.Patient;
import hospital.management.system.service.PatientService;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientListView extends JPanel {

    private final PatientService patientService;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientListView() {
        this.patientService = new PatientService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // Top panel for search/refresh (Search is placeholder for future enhancement)
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = UIComponentFactory.createPrimaryButton("Refresh Data", e -> loadData());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "ID Type", "ID Number", "Name", "Contact", "Gender", "Room", "Admission Date", "Deposit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = UIComponentFactory.createTableScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() {
                return patientService.getAllAdmittedPatients();
            }

            @Override
            protected void done() {
                try {
                    List<Patient> patients = get();
                    tableModel.setRowCount(0); // Clear existing
                    
                    for (Patient p : patients) {
                        tableModel.addRow(new Object[]{
                            p.getPatientId(),
                            p.getIdTypeName(),
                            p.getIdNumber(),
                            p.getFullName(),
                            p.getContact(),
                            p.getGenderName(),
                            p.getRoomId() != null ? "Room ID: " + p.getRoomId() : "N/A",
                            p.getAdmissionTime().toLocalDate().toString(),
                            "$" + p.getDeposit()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PatientListView.this, "Failed to load patient data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
