package hospital.management.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.formdev.flatlaf.FlatLightLaf;

public class UpdatePatientDetails extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JComboBox<String> patientComboBox;
    private JTextField idField, contactField, nameField, ageField, diseaseField, roomField, depositField;
    private JRadioButton maleRadio, femaleRadio;
    private ButtonGroup genderGroup;
    private JComboBox<String> roomTypeCombo;
    private Choice roomNumberChoice;

    public UpdatePatientDetails() {
        // Frame setup
        setTitle("LAST MOMENT HOSPITAL - Update Patient Details");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Update Patient Details");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Image panel
        try {
            ImageIcon originalIcon = new ImageIcon(ClassLoader.getSystemResource("icon/updated.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            gbc.gridx = 4;
            gbc.gridy = 0;
            gbc.gridheight = 10;
            gbc.fill = GridBagConstraints.VERTICAL;
            contentPanel.add(imageLabel, gbc);
        } catch (Exception e) {
            System.out.println("Image not found: icon/updated.png");
        }

        // Form fields - Column 0
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(createLabel("Select Patient:"), gbc);

        patientComboBox = new JComboBox<>();
        patientComboBox.setPreferredSize(new Dimension(250, 30));
        patientComboBox.setFont(FIELD_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(patientComboBox, gbc);

        // ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(createLabel("ID:"), gbc);

        idField = createTextField();
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(idField, gbc);

        // Contact
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(createLabel("Contact:"), gbc);

        contactField = createTextField();
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(contactField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 6;
        contentPanel.add(createLabel("Name:"), gbc);

        nameField = createTextField();
        gbc.gridx = 0;
        gbc.gridy = 7;
        contentPanel.add(nameField, gbc);

        // Form fields - Column 1
        // Gender
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(createLabel("Gender:"), gbc);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(BACKGROUND_COLOR);
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setFont(FIELD_FONT);
        femaleRadio.setFont(FIELD_FONT);
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(genderPanel, gbc);

        // Age
        gbc.gridx = 1;
        gbc.gridy = 2;
        contentPanel.add(createLabel("Age:"), gbc);

        ageField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        contentPanel.add(ageField, gbc);

        // Disease
        gbc.gridx = 1;
        gbc.gridy = 4;
        contentPanel.add(createLabel("Disease:"), gbc);

        diseaseField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = 5;
        contentPanel.add(diseaseField, gbc);

        // Room Type
        gbc.gridx = 1;
        gbc.gridy = 6;
        contentPanel.add(createLabel("Room Type:"), gbc);

        roomTypeCombo = new JComboBox<>(new String[]{"G Bed", "Private Room", "ICU Bed"});
        roomTypeCombo.setFont(FIELD_FONT);
        roomTypeCombo.addActionListener(e -> updateRoomNumbers());
        gbc.gridx = 1;
        gbc.gridy = 7;
        contentPanel.add(roomTypeCombo, gbc);

        // Room Number
        gbc.gridx = 1;
        gbc.gridy = 8;
        contentPanel.add(createLabel("Room Number:"), gbc);

        roomNumberChoice = new Choice();
        roomNumberChoice.setFont(FIELD_FONT);
        loadAvailableRooms();
        gbc.gridx = 1;
        gbc.gridy = 9;
        contentPanel.add(roomNumberChoice, gbc);

        // Form fields - Column 2 (continued)
        // Deposit
        gbc.gridx = 2;
        gbc.gridy = 4;
        contentPanel.add(createLabel("Deposit Amount:"), gbc);

        depositField = createTextField();
        gbc.gridx = 2;
        gbc.gridy = 5;
        contentPanel.add(depositField, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton checkButton = createStyledButton("LOAD", PRIMARY_COLOR);
        checkButton.addActionListener(e -> loadPatientDetails());
        buttonPanel.add(checkButton);

        JButton updateButton = createStyledButton("UPDATE", new Color(46, 204, 113)); // Green
        updateButton.addActionListener(e -> updatePatientDetails());
        buttonPanel.add(updateButton);

        JButton backButton = createStyledButton("BACK", new Color(231, 76, 60)); // Red
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load patient names
        loadPatientNames();

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(FIELD_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadPatientNames() {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery("SELECT Name FROM Patient_Info");
            patientComboBox.removeAllItems();
            while (rs.next()) {
                patientComboBox.addItem(rs.getString("Name"));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading patient names: " + e.getMessage());
        }
    }

    private void loadAvailableRooms() {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT room_no FROM Room WHERE Availability = 'Available'");
            roomNumberChoice.removeAll();
            while (rs.next()) {
                roomNumberChoice.add(rs.getString("room_no"));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading available rooms: " + e.getMessage());
        }
    }

    private void updateRoomNumbers() {
        String selectedType = (String) roomTypeCombo.getSelectedItem();
        String roomType = "";

        if (selectedType.equals("G Bed")) roomType = "G Bed%";
        else if (selectedType.equals("Private Room")) roomType = "Private Room";
        else if (selectedType.equals("ICU Bed")) roomType = "ICU Bed%";

        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT room_no FROM Room WHERE Availability = 'Available' AND Room_type LIKE '" + roomType + "'");
            roomNumberChoice.removeAll();
            while (rs.next()) {
                roomNumberChoice.add(rs.getString("room_no"));
            }
        } catch (Exception e) {
            showErrorDialog("Error updating room numbers: " + e.getMessage());
        }
    }

    private void loadPatientDetails() {
        String selectedPatient = (String) patientComboBox.getSelectedItem();
        if (selectedPatient == null || selectedPatient.isEmpty()) {
            showWarningDialog("Please select a patient first");
            return;
        }

        try {
            conn c = new conn();
            // Get patient details using prepared statement
            String query = "SELECT * FROM Patient_Info WHERE Name = ?";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, selectedPatient);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Fill all fields with patient data
                idField.setText(rs.getString("ID"));
                contactField.setText(rs.getString("Contact"));
                nameField.setText(rs.getString("Name"));

                // Set gender
                String gender = rs.getString("Gender");
                if (gender.equals("Male")) {
                    maleRadio.setSelected(true);
                } else if (gender.equals("Female")) {
                    femaleRadio.setSelected(true);
                }

                ageField.setText(rs.getString("Age"));
                diseaseField.setText(rs.getString("Patient_Disease"));

                // Set room information
                String roomNo = rs.getString("Room_Number");
                roomNumberChoice.select(roomNo);

                // Set room type based on room number
                ResultSet roomRs = c.statement.executeQuery(
                        "SELECT Room_type FROM Room WHERE room_no = '" + roomNo + "'");
                if (roomRs.next()) {
                    String roomType = roomRs.getString("Room_type");
                    if (roomType.contains("G Bed")) {
                        roomTypeCombo.setSelectedItem("G Bed");
                    } else if (roomType.contains("Private")) {
                        roomTypeCombo.setSelectedItem("Private Room");
                    } else if (roomType.contains("ICU")) {
                        roomTypeCombo.setSelectedItem("ICU Bed");
                    }
                }

                depositField.setText(rs.getString("Deposite"));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading patient details: " + e.getMessage());
        }
    }

    private void updatePatientDetails() {
        String selectedPatient = (String) patientComboBox.getSelectedItem();
        if (selectedPatient == null || selectedPatient.isEmpty()) {
            showWarningDialog("Please select a patient first");
            return;
        }

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            conn c = new conn();

            // Get old room number to update availability
            String oldRoomQuery = "SELECT Room_Number FROM Patient_Info WHERE Name = ?";
            PreparedStatement oldRoomStmt = c.connection.prepareStatement(oldRoomQuery);
            oldRoomStmt.setString(1, selectedPatient);
            ResultSet oldRoomRs = oldRoomStmt.executeQuery();
            String oldRoomNo = "";
            if (oldRoomRs.next()) {
                oldRoomNo = oldRoomRs.getString("Room_Number");
            }

            // Update patient info using prepared statement
            String updateQuery = "UPDATE Patient_Info SET " +
                    "ID = ?, " +
                    "Contact = ?, " +
                    "Name = ?, " +
                    "Gender = ?, " +
                    "Age = ?, " +
                    "Patient_Disease = ?, " +
                    "Room_Number = ?, " +
                    "Deposite = ? " +
                    "WHERE Name = ?";

            PreparedStatement pstmt = c.connection.prepareStatement(updateQuery);
            pstmt.setString(1, idField.getText());
            pstmt.setString(2, contactField.getText());
            pstmt.setString(3, nameField.getText());
            pstmt.setString(4, maleRadio.isSelected() ? "Male" : "Female");
            pstmt.setInt(5, Integer.parseInt(ageField.getText()));
            pstmt.setString(6, diseaseField.getText());
            pstmt.setString(7, roomNumberChoice.getSelectedItem());
            pstmt.setString(8, depositField.getText());
            pstmt.setString(9, selectedPatient);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Update room availability
                if (!oldRoomNo.isEmpty()) {
                    // Set old room to available
                    String freeRoomQuery = "UPDATE Room SET Availability = 'Available' WHERE room_no = ?";
                    PreparedStatement freeStmt = c.connection.prepareStatement(freeRoomQuery);
                    freeStmt.setString(1, oldRoomNo);
                    freeStmt.executeUpdate();
                }

                // Set new room to occupied
                String occupyRoomQuery = "UPDATE Room SET Availability = 'Occupied' WHERE room_no = ?";
                PreparedStatement occupyStmt = c.connection.prepareStatement(occupyRoomQuery);
                occupyStmt.setString(1, roomNumberChoice.getSelectedItem());
                occupyStmt.executeUpdate();

                showSuccessDialog("Patient details updated successfully!");
                loadPatientNames(); // Refresh patient list in case name changed
            } else {
                showWarningDialog("No patient record was updated");
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid age (numeric value)");
        } catch (SQLException e) {
            showErrorDialog("Database error: " + e.getMessage());
        } catch (Exception e) {
            showErrorDialog("Error updating patient: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (idField.getText().trim().isEmpty()) {
            showWarningDialog("Please enter ID");
            return false;
        }
        if (contactField.getText().trim().isEmpty() || contactField.getText().trim().length() != 10) {
            showWarningDialog("Please enter valid 10-digit contact number");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showWarningDialog("Please enter patient name");
            return false;
        }
        if (!maleRadio.isSelected() && !femaleRadio.isSelected()) {
            showWarningDialog("Please select gender");
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            showWarningDialog("Please enter age");
            return false;
        }
        try {
            Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            showWarningDialog("Age must be a number");
            return false;
        }
        if (diseaseField.getText().trim().isEmpty()) {
            showWarningDialog("Please enter disease");
            return false;
        }
        if (roomNumberChoice.getSelectedItem() == null) {
            showWarningDialog("Please select a room");
            return false;
        }
        if (depositField.getText().trim().isEmpty()) {
            showWarningDialog("Please enter deposit amount");
            return false;
        }
        try {
            Double.parseDouble(depositField.getText());
        } catch (NumberFormatException e) {
            showWarningDialog("Deposit must be a number");
            return false;
        }
        return true;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new UpdatePatientDetails());
    }
}