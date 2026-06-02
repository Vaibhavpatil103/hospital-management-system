package hospital.management.system.view;

import hospital.management.system.model.Patient;
import hospital.management.system.model.Room;
import hospital.management.system.service.PatientService;
import hospital.management.system.service.RoomService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.InputValidator.ValidationResult;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PatientUpdateView extends BaseFrame {

    private final PatientService patientService;
    private final RoomService roomService;
    private List<Patient> admittedPatients;
    private List<Room> availableRooms;
    
    private JComboBox<String> patientCombo;
    private JComboBox<String> idTypeCombo;
    private JTextField idNumberField;
    private JTextField contactField;
    private JTextField nameField;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private JTextField ageField;
    private JTextField diseaseField;
    private JComboBox<String> roomCombo;
    private JTextField depositField;
    
    private Integer oldRoomId;
    private int currentPatientId;

    public PatientUpdateView() {
        super("Update Patient Details", 900, 650);
        this.patientService = new PatientService();
        this.roomService = new RoomService();
        
        setupUI();
        loadInitialData();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout());

        JPanel formPanel = UIComponentFactory.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Select Patient
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIComponentFactory.createLabel("Select Patient:"), gbc);
        patientCombo = UIComponentFactory.createComboBox(new String[]{"Loading..."});
        patientCombo.addActionListener(e -> populatePatientDetails());
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(patientCombo, gbc);

        // ID Type
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("ID Type:"), gbc);
        idTypeCombo = UIComponentFactory.createComboBox(new String[]{"AADHAR", "VOTER_ID", "DRIVING_LICENSE", "PASSPORT"});
        gbc.gridx = 1;
        formPanel.add(idTypeCombo, gbc);

        // ID Number
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("ID Number:"), gbc);
        idNumberField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(idNumberField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIComponentFactory.createLabel("Contact Number:"), gbc);
        contactField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UIComponentFactory.createLabel("Full Name:"), gbc);
        nameField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Column 2
        // Gender
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("Gender:"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderPanel.setBackground(AppTheme.CARD_BACKGROUND);
        ButtonGroup genderGroup = new ButtonGroup();
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        otherRadio = new JRadioButton("Other");
        maleRadio.setBackground(AppTheme.CARD_BACKGROUND);
        femaleRadio.setBackground(AppTheme.CARD_BACKGROUND);
        otherRadio.setBackground(AppTheme.CARD_BACKGROUND);
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        gbc.gridx = 3;
        formPanel.add(genderPanel, gbc);

        // Age
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Age:"), gbc);
        ageField = UIComponentFactory.createTextField();
        gbc.gridx = 3;
        formPanel.add(ageField, gbc);

        // Disease
        gbc.gridx = 2; gbc.gridy = 3;
        formPanel.add(UIComponentFactory.createLabel("Disease:"), gbc);
        diseaseField = UIComponentFactory.createTextField();
        gbc.gridx = 3;
        formPanel.add(diseaseField, gbc);

        // Room
        gbc.gridx = 2; gbc.gridy = 4;
        formPanel.add(UIComponentFactory.createLabel("Update Room:"), gbc);
        roomCombo = UIComponentFactory.createComboBox(new String[]{});
        gbc.gridx = 3;
        formPanel.add(roomCombo, gbc);

        // Deposit
        gbc.gridx = 2; gbc.gridy = 5;
        formPanel.add(UIComponentFactory.createLabel("Deposit:"), gbc);
        depositField = UIComponentFactory.createTextField();
        gbc.gridx = 3;
        formPanel.add(depositField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton updateBtn = UIComponentFactory.createSuccessButton("Update Patient", e -> handleUpdate());
        JButton backBtn = UIComponentFactory.createDangerButton("Back", e -> dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(backBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadInitialData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                admittedPatients = patientService.getAllAdmittedPatients();
                availableRooms = roomService.getAvailableRooms();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    
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

                    roomCombo.removeAllItems();
                    for (Room r : availableRooms) {
                        roomCombo.addItem(r.getRoomNumber() + " (" + r.getRoomType() + ")");
                    }

                    if (!admittedPatients.isEmpty()) {
                        populatePatientDetails();
                    }
                } catch (Exception e) {
                    showError("Failed to load initial data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void populatePatientDetails() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || admittedPatients == null || admittedPatients.isEmpty()) return;
        
        Patient p = admittedPatients.get(idx);
        currentPatientId = p.getPatientId();
        oldRoomId = p.getRoomId();
        
        idTypeCombo.setSelectedItem(p.getIdType());
        idNumberField.setText(p.getIdNumber());
        contactField.setText(p.getContact());
        nameField.setText(p.getFullName());
        
        if ("MALE".equals(p.getGender())) maleRadio.setSelected(true);
        else if ("FEMALE".equals(p.getGender())) femaleRadio.setSelected(true);
        else otherRadio.setSelected(true);
        
        ageField.setText(String.valueOf(p.getAge()));
        diseaseField.setText(p.getDisease());
        depositField.setText(p.getDeposit().toString());

        // Add current room to combo box temporarily if not already there, so they can keep it
        if (p.getRoomId() != null) {
            String currentRoomLabel = "Keep Current (ID: " + p.getRoomId() + ")";
            boolean exists = false;
            for (int i = 0; i < roomCombo.getItemCount(); i++) {
                if (roomCombo.getItemAt(i).equals(currentRoomLabel)) exists = true;
            }
            if (!exists) {
                roomCombo.insertItemAt(currentRoomLabel, 0);
            }
            roomCombo.setSelectedIndex(0);
        }
    }

    private void handleUpdate() {
        if (!validateInputs()) return;

        int idx = patientCombo.getSelectedIndex();
        if (idx < 0) return;
        
        Patient patient = admittedPatients.get(idx);
        
        patient.setIdType((String) idTypeCombo.getSelectedItem());
        patient.setIdNumber(idNumberField.getText().trim());
        patient.setContact(contactField.getText().trim());
        patient.setFullName(nameField.getText().trim());
        
        if (maleRadio.isSelected()) patient.setGender("MALE");
        else if (femaleRadio.isSelected()) patient.setGender("FEMALE");
        else patient.setGender("OTHER");
        
        patient.setAge(Integer.parseInt(ageField.getText().trim()));
        patient.setDisease(diseaseField.getText().trim());
        patient.setDeposit(new BigDecimal(depositField.getText().trim()));

        // Room logic
        int roomIdx = roomCombo.getSelectedIndex();
        // If they selected something other than "Keep Current (index 0 if it was injected)", update room.
        // We know availableRooms matches roomCombo if we exclude the "Keep Current" item.
        boolean keepCurrent = roomCombo.getSelectedItem() != null && roomCombo.getSelectedItem().toString().startsWith("Keep Current");
        
        if (!keepCurrent) {
            // Need to adjust index if "Keep Current" is at 0
            int offset = (roomCombo.getItemAt(0) != null && roomCombo.getItemAt(0).startsWith("Keep Current")) ? 1 : 0;
            if (roomIdx - offset >= 0 && roomIdx - offset < availableRooms.size()) {
                patient.setRoomId(availableRooms.get(roomIdx - offset).getRoomId());
            }
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                patientService.updatePatient(patient, oldRoomId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    showSuccess("Patient updated successfully!");
                    loadInitialData(); // Refresh UI
                } catch (Exception e) {
                    showError("Update failed: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        };
        worker.execute();
    }

    private boolean validateInputs() {
        ValidationResult v;
        
        v = InputValidator.validateRequired(idNumberField.getText(), "ID Number");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validatePhone(contactField.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateRequired(nameField.getText(), "Full Name");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateAge(ageField.getText());
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validateRequired(diseaseField.getText(), "Disease");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }
        
        v = InputValidator.validatePositiveAmount(depositField.getText(), "Deposit");
        if (!v.isValid()) { showError(v.getErrorMessage()); return false; }

        return true;
    }
}
