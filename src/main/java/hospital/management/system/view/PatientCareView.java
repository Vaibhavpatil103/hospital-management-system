package hospital.management.system.view;

import hospital.management.system.model.LabTest;
import hospital.management.system.model.MedicalHistory;
import hospital.management.system.model.Prescription;
import hospital.management.system.service.PatientCareService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PatientCareView extends JPanel {
    private final PatientCareService patientCareService;
    private JTextField patientIdField;
    private JTable historyTable, prescriptionTable, labTestTable;
    private DefaultTableModel historyModel, prescriptionModel, labTestModel;

    public PatientCareView() {
        this.patientCareService = new PatientCareService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(UIComponentFactory.createLabel("Patient ID:"));
        patientIdField = UIComponentFactory.createTextField();
        patientIdField.setPreferredSize(new Dimension(150, 30));
        JButton loadBtn = UIComponentFactory.createPrimaryButton("Load Data", e -> loadPatientData());
        topPanel.add(patientIdField);
        topPanel.add(loadBtn);

        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Medical History", createHistoryPanel());
        tabbedPane.addTab("Prescriptions", createPrescriptionPanel());
        tabbedPane.addTab("Lab Tests", createLabTestPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        historyModel = new DefaultTableModel(new String[]{"ID", "Diagnosis", "Allergies", "Notes", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyModel);
        panel.add(UIComponentFactory.createTableScrollPane(historyTable), BorderLayout.CENTER);

        JButton addBtn = UIComponentFactory.createSuccessButton("Add History", e -> showHistoryDialog());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showHistoryDialog() {
        int pid = getSelectedPatientId();
        if (pid == -1) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add History", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBackground(AppTheme.BACKGROUND);
        
        JTextField diagField = UIComponentFactory.createTextField();
        JTextField allergField = UIComponentFactory.createTextField();
        JTextField notesField = UIComponentFactory.createTextField();

        p.add(UIComponentFactory.createLabel("Diagnosis:")); p.add(diagField);
        p.add(UIComponentFactory.createLabel("Allergies:")); p.add(allergField);
        p.add(UIComponentFactory.createLabel("Notes:")); p.add(notesField);

        JButton save = UIComponentFactory.createPrimaryButton("Save", e -> {
            MedicalHistory mh = new MedicalHistory();
            mh.setPatientId(pid);
            mh.setDiagnosis(diagField.getText());
            mh.setAllergies(allergField.getText());
            mh.setNotes(notesField.getText());
            mh.setRecordedDate(LocalDate.now());
            
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() { patientCareService.addMedicalHistory(mh); return null; }
                @Override protected void done() { d.dispose(); loadPatientData(); }
            }.execute();
        });
        p.add(save);
        d.add(p);
        d.setVisible(true);
    }

    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        prescriptionModel = new DefaultTableModel(new String[]{"ID", "Doctor ID", "Medicine", "Dosage", "Duration", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        prescriptionTable = new JTable(prescriptionModel);
        panel.add(UIComponentFactory.createTableScrollPane(prescriptionTable), BorderLayout.CENTER);

        JButton addBtn = UIComponentFactory.createSuccessButton("Add Prescription", e -> showPrescriptionDialog());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showPrescriptionDialog() {
        int pid = getSelectedPatientId();
        if (pid == -1) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Prescription", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBackground(AppTheme.BACKGROUND);
        
        JTextField docField = UIComponentFactory.createTextField();
        JTextField medField = UIComponentFactory.createTextField();
        JTextField doseField = UIComponentFactory.createTextField();
        JTextField durField = UIComponentFactory.createTextField();

        p.add(UIComponentFactory.createLabel("Doctor ID:")); p.add(docField);
        p.add(UIComponentFactory.createLabel("Medicine:")); p.add(medField);
        p.add(UIComponentFactory.createLabel("Dosage:")); p.add(doseField);
        p.add(UIComponentFactory.createLabel("Duration:")); p.add(durField);

        JButton save = UIComponentFactory.createPrimaryButton("Save", e -> {
            Prescription pr = new Prescription();
            pr.setPatientId(pid);
            try { pr.setDoctorId(Integer.parseInt(docField.getText())); } catch (Exception ex) { return; }
            pr.setMedicineName(medField.getText());
            pr.setDosage(doseField.getText());
            pr.setDuration(durField.getText());
            pr.setPrescribedDate(LocalDate.now());
            pr.setNotes("");
            
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() { patientCareService.addPrescription(pr); return null; }
                @Override protected void done() { d.dispose(); loadPatientData(); }
            }.execute();
        });
        p.add(save);
        d.add(p);
        d.setVisible(true);
    }

    private JPanel createLabTestPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        labTestModel = new DefaultTableModel(new String[]{"ID", "Test Name", "Result", "Date", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        labTestTable = new JTable(labTestModel);
        panel.add(UIComponentFactory.createTableScrollPane(labTestTable), BorderLayout.CENTER);

        JButton addBtn = UIComponentFactory.createSuccessButton("Order Test", e -> showLabTestDialog());
        JButton updateBtn = UIComponentFactory.createPrimaryButton("Update Result", e -> updateLabResult());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void showLabTestDialog() {
        int pid = getSelectedPatientId();
        if (pid == -1) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order Lab Test", true);
        d.setSize(400, 200);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setBackground(AppTheme.BACKGROUND);
        
        JTextField testField = UIComponentFactory.createTextField();

        p.add(UIComponentFactory.createLabel("Test Name:")); p.add(testField);

        JButton save = UIComponentFactory.createPrimaryButton("Save", e -> {
            LabTest lt = new LabTest();
            lt.setPatientId(pid);
            lt.setTestName(testField.getText());
            lt.setTestDate(LocalDate.now());
            
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() { patientCareService.orderLabTest(lt); return null; }
                @Override protected void done() { d.dispose(); loadPatientData(); }
            }.execute();
        });
        p.add(save);
        d.add(p);
        d.setVisible(true);
    }
    
    private void updateLabResult() {
        int row = labTestTable.getSelectedRow();
        if (row < 0) return;
        int testId = (int) labTestModel.getValueAt(row, 0);
        String res = JOptionPane.showInputDialog(this, "Enter Result:");
        if (res == null || res.trim().isEmpty()) return;
        
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { patientCareService.updateLabTestResult(testId, res); return null; }
            @Override protected void done() { loadPatientData(); }
        }.execute();
    }

    private int getSelectedPatientId() {
        try {
            return Integer.parseInt(patientIdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID first.");
            return -1;
        }
    }

    private void loadPatientData() {
        int pid = getSelectedPatientId();
        if (pid == -1) return;

        new SwingWorker<Void, Void>() {
            List<MedicalHistory> histories;
            List<Prescription> prescriptions;
            List<LabTest> labTests;

            @Override
            protected Void doInBackground() {
                histories = patientCareService.getMedicalHistory(pid);
                prescriptions = patientCareService.getPrescriptions(pid);
                labTests = patientCareService.getLabTests(pid);
                return null;
            }

            @Override
            protected void done() {
                historyModel.setRowCount(0);
                for (MedicalHistory m : histories) {
                    historyModel.addRow(new Object[]{m.getHistoryId(), m.getDiagnosis(), m.getAllergies(), m.getNotes(), m.getRecordedDate()});
                }
                
                prescriptionModel.setRowCount(0);
                for (Prescription p : prescriptions) {
                    prescriptionModel.addRow(new Object[]{p.getPrescriptionId(), p.getDoctorId(), p.getMedicineName(), p.getDosage(), p.getDuration(), p.getPrescribedDate()});
                }
                
                labTestModel.setRowCount(0);
                for (LabTest l : labTests) {
                    labTestModel.addRow(new Object[]{l.getTestId(), l.getTestName(), l.getTestResult(), l.getTestDate(), l.getStatus()});
                }
            }
        }.execute();
    }
}
