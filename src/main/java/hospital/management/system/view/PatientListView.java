package hospital.management.system.view;

import hospital.management.system.model.Patient;
import hospital.management.system.service.PatientService;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientListView extends BaseFrame {

    private final PatientService patientService;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientListView() {
        super("All Admitted Patients", 1000, 600);
        this.patientService = new PatientService();
        setupUI();
        loadData();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout(0, 10));

        // Top panel for search/refresh (Search is placeholder for future enhancement)
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = UIComponentFactory.createPrimaryButton("Refresh Data", e -> loadData());
        topPanel.add(refreshBtn);
        contentPanel.add(topPanel, BorderLayout.NORTH);

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
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton backBtn = UIComponentFactory.createDangerButton("Back", e -> dispose());
        buttonPanel.add(backBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
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
                            p.getIdType(),
                            p.getIdNumber(),
                            p.getFullName(),
                            p.getContact(),
                            p.getGender(),
                            p.getRoomId() != null ? "Room ID: " + p.getRoomId() : "N/A",
                            p.getAdmissionTime().toLocalDate().toString(),
                            "$" + p.getDeposit()
                        });
                    }
                } catch (Exception e) {
                    showError("Failed to load patient data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
