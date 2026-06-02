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
import java.time.LocalDateTime;
import java.util.List;

public class PatientRegistrationView extends BaseFrame {

    private final PatientService patientService;
    private final RoomService roomService;

    private JComboBox<String> idTypeCombo;
    private JTextField idNumberField;
    private JTextField contactField;
    private JTextField nameField;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private JTextField ageField;
    private JTextField diseaseField;
    private JComboBox<String> roomCombo;
    private JTextField depositField;
    private List<Room> availableRooms;

    public PatientRegistrationView() {
        super("Patient Registration", 900, 600);
        this.patientService = new PatientService();
        this.roomService = new RoomService();
        
        setupUI();
        loadAvailableRooms();
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

        // ID Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIComponentFactory.createLabel("ID Type:"), gbc);
        idTypeCombo = UIComponentFactory.createComboBox(new String[]{"AADHAR", "VOTER_ID", "DRIVING_LICENSE", "PASSPORT"});
        gbc.gridx = 1;
        formPanel.add(idTypeCombo, gbc);

        // ID Number
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("ID Number:"), gbc);
        idNumberField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(idNumberField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Contact Number:"), gbc);
        contactField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIComponentFactory.createLabel("Full Name:"), gbc);
        nameField = UIComponentFactory.createTextField();
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 4;
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
        maleRadio.setSelected(true);
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        gbc.gridx = 1;
        formPanel.add(genderPanel, gbc);

        // Column 2
        // Age
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(UIComponentFactory.createLabel("Age:"), gbc);
        ageField = UIComponentFactory.createTextField();
        gbc.gridx = 3;
        formPanel.add(ageField, gbc);

        // Disease
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(UIComponentFactory.createLabel("Disease:"), gbc);
        diseaseField = UIComponentFactory.createTextField();
        gbc.gridx = 3;
        formPanel.add(diseaseField, gbc);

        // Room
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Assign Room:"), gbc);
        roomCombo = UIComponentFactory.createComboBox(new String[]{"Loading..."});
        gbc.gridx = 3;
        formPanel.add(roomCombo, gbc);

        // Deposit
        gbc.gridx = 2; gbc.gridy = 3;
        formPanel.add(UIComponentFactory.createLabel("Initial Deposit:"), gbc);
        depositField = UIComponentFactory.createTextField();
        depositField.setText("0.00");
        gbc.gridx = 3;
        formPanel.add(depositField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton saveBtn = UIComponentFactory.createPrimaryButton("Register Patient", e -> handleSave());
        JButton clearBtn = UIComponentFactory.createSecondaryButton("Clear Form", e -> clearForm());
        JButton backBtn = UIComponentFactory.createDangerButton("Back", e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableRooms() {
        SwingWorker<List<Room>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Room> doInBackground() {
                return roomService.getAvailableRooms();
            }

            @Override
            protected void done() {
                try {
                    availableRooms = get();
                    roomCombo.removeAllItems();
                    
                    if (availableRooms.isEmpty()) {
                        roomCombo.addItem("No rooms available");
                        roomCombo.setEnabled(false);
                    } else {
                        for (Room r : availableRooms) {
                            roomCombo.addItem(r.getRoomNumber() + " (" + r.getRoomType() + " - $" + r.getPricePerDay() + ")");
                        }
                        roomCombo.setEnabled(true);
                    }
                } catch (Exception e) {
                    showError("Failed to load rooms: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSave() {
        // Validate inputs
        if (!validateInputs()) return;

        Patient patient = new Patient();
        patient.setIdType((String) idTypeCombo.getSelectedItem());
        patient.setIdNumber(idNumberField.getText().trim());
        patient.setContact(contactField.getText().trim());
        patient.setFullName(nameField.getText().trim());
        
        if (maleRadio.isSelected()) patient.setGender("MALE");
        else if (femaleRadio.isSelected()) patient.setGender("FEMALE");
        else patient.setGender("OTHER");
        
        patient.setAge(Integer.parseInt(ageField.getText().trim()));
        patient.setDisease(diseaseField.getText().trim());
        
        if (!availableRooms.isEmpty() && roomCombo.getSelectedIndex() >= 0) {
            patient.setRoomId(availableRooms.get(roomCombo.getSelectedIndex()).getRoomId());
        }
        
        patient.setDeposit(new BigDecimal(depositField.getText().trim()));
        patient.setStatus("ADMITTED");
        patient.setAdmissionTime(LocalDateTime.now());

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                patientService.registerPatient(patient);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    showSuccess("Patient registered successfully!");
                    dispose();
                } catch (Exception e) {
                    showError("Registration failed: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
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
        
        if (availableRooms == null || availableRooms.isEmpty()) {
            showError("Cannot register patient without assigning an available room.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        idTypeCombo.setSelectedIndex(0);
        idNumberField.setText("");
        contactField.setText("");
        nameField.setText("");
        maleRadio.setSelected(true);
        ageField.setText("");
        diseaseField.setText("");
        if (roomCombo.getItemCount() > 0) roomCombo.setSelectedIndex(0);
        depositField.setText("0.00");
    }
}
