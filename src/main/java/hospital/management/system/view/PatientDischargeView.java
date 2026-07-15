package hospital.management.system.view;

import hospital.management.system.model.Bill;
import hospital.management.system.model.Patient;
import hospital.management.system.service.BillingService;
import hospital.management.system.service.DischargeService;
import hospital.management.system.service.PatientService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.SessionManager;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PatientDischargeView extends BaseFrame {

    private final PatientService patientService;
    private final DischargeService dischargeService;
    private final BillingService billingService;
    
    private List<Patient> admittedPatients;
    private JComboBox<String> patientCombo;
    
    private JLabel nameLabel, genderLabel, diseaseLabel, roomLabel, depositLabel;
    private JLabel billTotalLabel, balanceDueLabel;
    
    private Bill currentBill;

    public PatientDischargeView() {
        super("Discharge Patient", 900, 600);
        this.patientService = new PatientService();
        this.dischargeService = new DischargeService();
        this.billingService = new BillingService();
        
        setupUI();
        loadPatients();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout());

        JPanel formPanel = UIComponentFactory.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Select Patient
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIComponentFactory.createLabel("Select Admitted Patient:"), gbc);
        patientCombo = UIComponentFactory.createComboBox(new String[]{"Loading..."});
        patientCombo.addActionListener(e -> displayPatientDetails());
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(patientCombo, gbc);

        // Details Panel (Left)
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("Patient Name:"), gbc);
        nameLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Gender:"), gbc);
        genderLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1;
        formPanel.add(genderLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIComponentFactory.createLabel("Disease:"), gbc);
        diseaseLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1;
        formPanel.add(diseaseLabel, gbc);

        // Details Panel (Right)
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("Room ID:"), gbc);
        roomLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 3;
        formPanel.add(roomLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Deposit Paid:"), gbc);
        depositLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 3;
        formPanel.add(depositLabel, gbc);

        // Billing Summary
        JPanel billPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        billPanel.setBackground(new Color(255, 250, 240));
        billPanel.setBorder(BorderFactory.createTitledBorder("Discharge Bill Summary"));
        
        billPanel.add(UIComponentFactory.createLabel("Total Bill:"));
        billTotalLabel = new JLabel("$0.00");
        billTotalLabel.setFont(AppTheme.HEADER_FONT);
        billTotalLabel.setForeground(AppTheme.DANGER);
        billPanel.add(billTotalLabel);
        
        billPanel.add(UIComponentFactory.createLabel("Balance Due:"));
        balanceDueLabel = new JLabel("$0.00");
        balanceDueLabel.setFont(AppTheme.HEADER_FONT);
        balanceDueLabel.setForeground(AppTheme.DANGER);
        billPanel.add(balanceDueLabel);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(billPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton calcBillBtn = UIComponentFactory.createPrimaryButton("Calculate Final Bill", e -> calculateBill());
        JButton dischargeBtn = UIComponentFactory.createSuccessButton("Confirm Discharge", e -> dischargePatient());
        JButton backBtn = UIComponentFactory.createDangerButton("Back", e -> dispose());
        
        buttonPanel.add(calcBillBtn);
        buttonPanel.add(dischargeBtn);
        buttonPanel.add(backBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() {
                return patientService.getAllAdmittedPatients();
            }

            @Override
            protected void done() {
                try {
                    admittedPatients = get();
                    patientCombo.removeAllItems();
                    
                    if (admittedPatients.isEmpty()) {
                        patientCombo.addItem("No admitted patients found");
                        patientCombo.setEnabled(false);
                    } else {
                        for (Patient p : admittedPatients) {
                            patientCombo.addItem(p.getFullName() + " (ID: " + p.getPatientId() + ")");
                        }
                        patientCombo.setEnabled(true);
                    }
                } catch (Exception e) {
                    showError("Failed to load patients: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayPatientDetails() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || admittedPatients == null || admittedPatients.isEmpty()) return;
        
        Patient p = admittedPatients.get(idx);
        nameLabel.setText(p.getFullName());
        genderLabel.setText(p.getGenderName());
        diseaseLabel.setText(p.getDisease());
        roomLabel.setText(p.getRoomId() != null ? String.valueOf(p.getRoomId()) : "N/A");
        depositLabel.setText("$" + p.getDeposit().toString());
        
        // Reset bill display
        currentBill = null;
        billTotalLabel.setText("$0.00");
        balanceDueLabel.setText("$0.00");
    }

    private void calculateBill() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || admittedPatients == null || admittedPatients.isEmpty()) return;
        
        Patient p = admittedPatients.get(idx);
        
        try {
            currentBill = billingService.generateBill(p.getPatientId());
            billTotalLabel.setText("$" + currentBill.getTotalAmount().toString());
            balanceDueLabel.setText("$" + currentBill.getBalanceDue().toString());
        } catch (Exception e) {
            showError("Failed to calculate bill: " + e.getMessage());
        }
    }

    private void dischargePatient() {
        if (currentBill == null) {
            showWarning("Please calculate the final bill first.");
            return;
        }

        int idx = patientCombo.getSelectedIndex();
        if (idx < 0) return;
        
        Patient p = admittedPatients.get(idx);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to discharge " + p.getFullName() + "?\nThis will free up their room.", 
                "Confirm Discharge", JOptionPane.YES_NO_OPTION);
                
        if (confirm != JOptionPane.YES_OPTION) return;

        int userId = SessionManager.getCurrentUser().getUserId();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                dischargeService.dischargePatient(p, currentBill, userId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    showSuccess("Patient discharged successfully.");
                    loadPatients(); // Refresh list
                    
                    nameLabel.setText("");
                    genderLabel.setText("");
                    diseaseLabel.setText("");
                    roomLabel.setText("");
                    depositLabel.setText("");
                    billTotalLabel.setText("$0.00");
                    balanceDueLabel.setText("$0.00");
                    currentBill = null;
                    
                } catch (Exception e) {
                    showError("Discharge failed: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        };
        worker.execute();
    }
}
