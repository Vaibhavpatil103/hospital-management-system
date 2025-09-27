package hospital.management.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.formdev.flatlaf.FlatLightLaf;

public class PatientDischarge extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JComboBox<String> patientIdCombo;
    private JLabel patientNameLabel, genderLabel, diseaseLabel, roomNumberLabel, inTimeLabel, outTimeLabel, depositLabel;

    public PatientDischarge() {
        setTitle("LAST MOMENT HOSPITAL - Patient Discharge");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titleLabel = new JLabel("Patient Discharge");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Patient ID Selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Patient ID:"), gbc);
        patientIdCombo = new JComboBox<>();
        patientIdCombo.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(patientIdCombo, gbc);

        // Patient Details
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Patient Name:"), gbc);
        patientNameLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(patientNameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Gender:"), gbc);
        genderLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(genderLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Disease:"), gbc);
        diseaseLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(diseaseLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createLabel("Room Number:"), gbc);
        roomNumberLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(roomNumberLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createLabel("Check-In Time:"), gbc);
        inTimeLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(inTimeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createLabel("Deposit Amount:"), gbc);
        depositLabel = createValueLabel();
        gbc.gridx = 1;
        formPanel.add(depositLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(createLabel("Check-Out Time:"), gbc);
        outTimeLabel = createValueLabel();
        outTimeLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        gbc.gridx = 1;
        formPanel.add(outTimeLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton checkButton = createStyledButton("CHECK", PRIMARY_COLOR);
        checkButton.addActionListener(e -> checkPatientDetails());
        buttonPanel.add(checkButton);

        JButton dischargeButton = createStyledButton("DISCHARGE", new Color(46, 204, 113));
        dischargeButton.addActionListener(e -> dischargePatient());
        buttonPanel.add(dischargeButton);

        JButton backButton = createStyledButton("BACK", new Color(231, 76, 60));
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadPatientIds();
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(PRIMARY_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
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

    private void loadPatientIds() {
        try (Connection c = new conn().connection;
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID FROM patient_info WHERE Room_Number IS NOT NULL")) {
            patientIdCombo.removeAllItems();
            while (rs.next()) {
                patientIdCombo.addItem(rs.getString("ID"));
            }
            if (patientIdCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No admitted patients found", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patient IDs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkPatientDetails() {
        String patientId = (String) patientIdCombo.getSelectedItem();
        if (patientId == null || patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a patient ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection c = new conn().connection;
             PreparedStatement stmt = c.prepareStatement(
                     "SELECT Name, Gender, Patient_Disease, Room_Number, Time, Deposite " +
                             "FROM patient_info WHERE ID = ?")) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patientNameLabel.setText(rs.getString("Name"));
                genderLabel.setText(rs.getString("Gender"));
                diseaseLabel.setText(rs.getString("Patient_Disease"));
                roomNumberLabel.setText(rs.getString("Room_Number"));
                inTimeLabel.setText(rs.getString("Time"));
                depositLabel.setText(rs.getString("Deposite"));
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving patient details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dischargePatient() {
        String patientId = (String) patientIdCombo.getSelectedItem();
        String roomNumber = roomNumberLabel.getText();

        if (patientId == null || patientId.isEmpty() || roomNumber == null || roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please check patient details first", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to discharge this patient?\nPatient ID: " + patientId +
                        "\nRoom Number: " + roomNumber, "Confirm Discharge", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection c = new conn().connection) {
            c.setAutoCommit(false);

            try {
                // Insert into discharge records
                try (PreparedStatement insertStmt = c.prepareStatement(
                        "INSERT INTO patient_discharge (patient_id, patient_name, gender, disease, " +
                                "room_number, check_in_time, check_out_time, deposit) " +
                                "SELECT ID, Name, Gender, Patient_Disease, Room_Number, Time, ?, Deposite " +
                                "FROM patient_info WHERE ID = ?")) {
                    insertStmt.setString(1, outTimeLabel.getText());
                    insertStmt.setString(2, patientId);
                    insertStmt.executeUpdate();
                }

                // Delete from patient_info
                try (PreparedStatement deleteStmt = c.prepareStatement(
                        "DELETE FROM patient_info WHERE ID = ?")) {
                    deleteStmt.setString(1, patientId);
                    int deleted = deleteStmt.executeUpdate();
                    if (deleted == 0) {
                        throw new SQLException("Patient not found");
                    }
                }

                // Update room availability
                try (PreparedStatement updateStmt = c.prepareStatement(
                        "UPDATE room SET Availability = 'Available' WHERE room_no = ?")) {
                    updateStmt.setString(1, roomNumber);
                    updateStmt.executeUpdate();
                }

                c.commit();
                JOptionPane.showMessageDialog(this, "Patient discharged successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatientIds(); // Refresh patient list
                clearPatientDetails();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error discharging patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPatientDetails() {
        patientNameLabel.setText("");
        genderLabel.setText("");
        diseaseLabel.setText("");
        roomNumberLabel.setText("");
        inTimeLabel.setText("");
        depositLabel.setText("");
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

        SwingUtilities.invokeLater(() -> new PatientDischarge());
    }
}