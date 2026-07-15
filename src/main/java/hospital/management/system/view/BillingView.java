package hospital.management.system.view;

import hospital.management.system.model.Bill;
import hospital.management.system.model.BillStatus;
import hospital.management.system.model.Patient;
import hospital.management.system.service.BillingService;
import hospital.management.system.service.PatientService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class BillingView extends JPanel {

    private final PatientService patientService;
    private final BillingService billingService;
    
    private JComboBox<String> patientCombo;
    private List<Patient> patients;
    
    private JLabel billIdLabel, statusLabel;
    private JLabel roomChargesLabel, doctorFeesLabel, otherChargesLabel;
    private JLabel totalLabel, depositLabel, balanceLabel;
    
    private Bill currentBill;

    public BillingView() {
        this.patientService = new PatientService();
        this.billingService = new BillingService();
        
        setupUI();
        loadPatients();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 15));

        // Top Selection Panel
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(UIComponentFactory.createLabel("Select Patient:"));
        patientCombo = UIComponentFactory.createComboBox(new String[]{"Loading..."});
        patientCombo.addActionListener(e -> fetchBill());
        topPanel.add(patientCombo);
        add(topPanel, BorderLayout.NORTH);

        // Bill Details Panel
        JPanel detailsPanel = UIComponentFactory.createCardPanel();
        detailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Bill ID:"), gbc);
        billIdLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1; detailsPanel.add(billIdLabel, gbc);
        
        gbc.gridx = 2; detailsPanel.add(UIComponentFactory.createLabel("Status:"), gbc);
        statusLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 3; detailsPanel.add(statusLabel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Room Charges:"), gbc);
        roomChargesLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1; detailsPanel.add(roomChargesLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Doctor Fees:"), gbc);
        doctorFeesLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1; detailsPanel.add(doctorFeesLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Other Charges:"), gbc);
        otherChargesLabel = UIComponentFactory.createValueLabel();
        gbc.gridx = 1; detailsPanel.add(otherChargesLabel, gbc);
        
        // Separator
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        detailsPanel.add(new JSeparator(), gbc);
        
        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Total Amount:"), gbc);
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(AppTheme.HEADER_FONT);
        gbc.gridx = 1; detailsPanel.add(totalLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Deposit Paid:"), gbc);
        depositLabel = new JLabel("$0.00");
        depositLabel.setFont(AppTheme.HEADER_FONT);
        depositLabel.setForeground(AppTheme.SUCCESS);
        gbc.gridx = 1; detailsPanel.add(depositLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(UIComponentFactory.createLabel("Balance Due:"), gbc);
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(AppTheme.HEADER_FONT);
        balanceLabel.setForeground(AppTheme.DANGER);
        gbc.gridx = 1; detailsPanel.add(balanceLabel, gbc);

        add(detailsPanel, BorderLayout.CENTER);

        // Footer buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton generateBtn = UIComponentFactory.createPrimaryButton("Generate Bill", e -> generateBillForPatient());
        JButton payBtn = UIComponentFactory.createSuccessButton("Mark as Paid", e -> markAsPaid());
        JButton printBtn = UIComponentFactory.createPrimaryButton("Print Invoice", e -> printInvoice());
        
        buttonPanel.add(generateBtn);
        buttonPanel.add(payBtn);
        buttonPanel.add(printBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() {
                // Fetch all patients (admitted and discharged) so we can see past bills
                return patientService.getAllPatients();
            }

            @Override
            protected void done() {
                try {
                    patients = get();
                    patientCombo.removeAllItems();
                    if (patients.isEmpty()) {
                        patientCombo.addItem("No patients found");
                        patientCombo.setEnabled(false);
                    } else {
                        for (Patient p : patients) {
                            patientCombo.addItem(p.getFullName() + " (ID: " + p.getPatientId() + ")");
                        }
                        patientCombo.setEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BillingView.this, "Failed to load patients.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void fetchBill() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || patients == null || patients.isEmpty()) return;
        
        int patientId = patients.get(idx).getPatientId();
        
        SwingWorker<Optional<Bill>, Void> worker = new SwingWorker<>() {
            @Override
            protected Optional<Bill> doInBackground() {
                return billingService.getLatestBillForPatient(patientId);
            }

            @Override
            protected void done() {
                try {
                    Optional<Bill> billOpt = get();
                    if (billOpt.isPresent()) {
                        currentBill = billOpt.get();
                        billIdLabel.setText(String.valueOf(currentBill.getBillId()));
                        statusLabel.setText(currentBill.getStatusName());
                        if (BillStatus.PAID == currentBill.getStatus()) {
                            statusLabel.setForeground(AppTheme.SUCCESS);
                        } else {
                            statusLabel.setForeground(AppTheme.WARNING);
                        }
                        
                        roomChargesLabel.setText("$" + currentBill.getRoomCharges());
                        doctorFeesLabel.setText("$" + currentBill.getDoctorFees());
                        otherChargesLabel.setText("$" + currentBill.getOtherCharges());
                        totalLabel.setText("$" + currentBill.getTotalAmount());
                        depositLabel.setText("$" + currentBill.getDepositPaid());
                        balanceLabel.setText("$" + currentBill.getBalanceDue());
                    } else {
                        currentBill = null;
                        clearBillDisplay();
                        billIdLabel.setText("No Bill Generated");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BillingView.this, "Failed to fetch bill.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void clearBillDisplay() {
        billIdLabel.setText("");
        statusLabel.setText("");
        roomChargesLabel.setText("");
        doctorFeesLabel.setText("");
        otherChargesLabel.setText("");
        totalLabel.setText("$0.00");
        depositLabel.setText("$0.00");
        balanceLabel.setText("$0.00");
    }

    private void markAsPaid() {
        if (currentBill == null) {
            JOptionPane.showMessageDialog(this, "No bill selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (BillStatus.PAID == currentBill.getStatus()) {
            JOptionPane.showMessageDialog(this, "Bill is already paid.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                billingService.markBillAsPaid(currentBill.getBillId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(BillingView.this, "Bill marked as PAID successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    fetchBill(); // Refresh display
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BillingView.this, "Failed to mark bill as paid: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void generateBillForPatient() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || patients == null || patients.isEmpty()) return;
        
        int patientId = patients.get(idx).getPatientId();
        
        if (currentBill != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "A bill already exists. Generate a new one? (Old one will be overridden or just create a new record)", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                billingService.generateBill(patientId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(BillingView.this, "Bill generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    fetchBill(); // Refresh display
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BillingView.this, "Failed to generate bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void printInvoice() {
        if (currentBill == null) {
            JOptionPane.showMessageDialog(this, "No bill selected to print.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // In a real app, this would use a reporting library like JasperReports or create a PDF.
        JOptionPane.showMessageDialog(this, "Invoice printing simulated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
