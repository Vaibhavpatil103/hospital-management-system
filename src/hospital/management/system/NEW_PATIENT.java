package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.formdev.flatlaf.FlatLightLaf;

public class NEW_PATIENT extends JFrame implements ActionListener {

    // Form fields
    private JTextField textFieldID, textFieldContact, textName, textFieldAge, textFieldDisease, textFieldDeposite;
    private JRadioButton r1, r2;
    private Choice roomChoice;
    private JLabel dateLabel;
    private JButton btnAdd, btnBack, btnClear;
    private JComboBox<String> idTypeCombo;

    // Constants
    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Tahoma", Font.PLAIN, 14);

    public NEW_PATIENT() {
        setTitle("New Patient Registration");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 590);
        panel.setBackground(BACKGROUND_COLOR);
        panel.setLayout(null);
        add(panel);

        // Image
        addPatientImage(panel);

        // Title
        addTitleLabel(panel);

        // Form fields
        addFormFields(panel);

        // Buttons
        addButtons(panel);

        setUndecorated(true);
        setVisible(true);
    }

    private void addPatientImage(JPanel panel) {
        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/patient.png"));
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(600, 150, 200, 200);
        panel.add(label);
    }

    private void addTitleLabel(JPanel panel) {
        JLabel titleLabel = new JLabel("NEW PATIENT REGISTRATION");
        titleLabel.setBounds(118, 11, 350, 53);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel);
    }

    private void addFormFields(JPanel panel) {
        // ID Type and Number
        addLabel(panel, "ID Type:", 35, 70);
        idTypeCombo = new JComboBox<>(new String[]{"Aadhar Card", "Voter ID", "Driving License", "Passport"});
        addComponent(panel, idTypeCombo, 200, 70, 150, 25);

        addLabel(panel, "ID Number:", 35, 110);
        textFieldID = new JTextField();
        addComponent(panel, textFieldID, 200, 110, 200, 25);

        // Contact
        addLabel(panel, "Contact No:", 35, 150);
        textFieldContact = new JTextField();
        addComponent(panel, textFieldContact, 200, 150, 200, 25);

        // Name
        addLabel(panel, "Full Name:", 35, 190);
        textName = new JTextField();
        addComponent(panel, textName, 200, 190, 200, 25);

        // Gender
        addLabel(panel, "Gender:", 35, 230);
        JPanel genderPanel = new JPanel();
        genderPanel.setBounds(200, 230, 200, 25);
        genderPanel.setBackground(BACKGROUND_COLOR);
        genderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        ButtonGroup genderGroup = new ButtonGroup();
        r1 = new JRadioButton("Male");
        r2 = new JRadioButton("Female");
        r1.setFont(FIELD_FONT);
        r2.setFont(FIELD_FONT);
        genderGroup.add(r1);
        genderGroup.add(r2);
        genderPanel.add(r1);
        genderPanel.add(r2);
        panel.add(genderPanel);

        // Age
        addLabel(panel, "Age:", 35, 270);
        textFieldAge = new JTextField();
        addComponent(panel, textFieldAge, 200, 270, 50, 25);

        // Disease
        addLabel(panel, "Disease:", 35, 310);
        textFieldDisease = new JTextField();
        addComponent(panel, textFieldDisease, 200, 310, 200, 25);

        // Room selection
        addLabel(panel, "Room Number:", 35, 350);
        roomChoice = new Choice();
        loadAvailableRooms(roomChoice);
        roomChoice.setBounds(200, 350, 150, 25);
        roomChoice.setFont(FIELD_FONT);
        panel.add(roomChoice);

        // Current Date and Time
        addLabel(panel, "Admission Time:", 35, 390);
        dateLabel = new JLabel();
        updateDateTime();
        dateLabel.setBounds(200, 390, 250, 25);
        dateLabel.setFont(FIELD_FONT);
        panel.add(dateLabel);

        // Deposite
        addLabel(panel, "Deposit Amount:", 35, 430);
        textFieldDeposite = new JTextField();
        addComponent(panel, textFieldDeposite, 200, 430, 150, 25);
    }

    private void addButtons(JPanel panel) {
        btnAdd = createButton("ADD", 150, 490, PRIMARY_COLOR);
        btnBack = createButton("BACK", 300, 490, new Color(108, 117, 125));
        btnClear = createButton("CLEAR", 450, 490, new Color(40, 167, 69));

        panel.add(btnAdd);
        panel.add(btnBack);
        panel.add(btnClear);

        btnAdd.addActionListener(this);
        btnBack.addActionListener(this);
        btnClear.addActionListener(this);
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 150, 20);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR);
        panel.add(label);
    }

    private void addComponent(JPanel panel, JComponent component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        component.setFont(FIELD_FONT);
        panel.add(component);
    }

    private JButton createButton(String text, int x, int y, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 120, 35);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void loadAvailableRooms(Choice roomChoice) {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery("SELECT room_no FROM Room WHERE Availability = 'Available'");
            roomChoice.removeAll();
            while (rs.next()) {
                roomChoice.add(rs.getString("room_no"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateLabel.setText(sdf.format(new Date()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            registerPatient();
        } else if (e.getSource() == btnBack) {
            dispose();
        } else if (e.getSource() == btnClear) {
            clearForm();
        }
    }

    private void registerPatient() {
        if (!validateInputs()) {
            return;
        }

        String idType = (String) idTypeCombo.getSelectedItem();
        String idNumber = textFieldID.getText().trim();
        String contact = textFieldContact.getText().trim();
        String name = textName.getText().trim();
        String gender = r1.isSelected() ? "Male" : (r2.isSelected() ? "Female" : "");
        String age = textFieldAge.getText().trim();
        String disease = textFieldDisease.getText().trim();
        String room = roomChoice.getSelectedItem();
        String deposit = textFieldDeposite.getText().trim();
        String time = dateLabel.getText();

        try {
            conn c = new conn();

            // Check if patient with same ID already exists
            if (patientExists(c, idNumber)) {
                JOptionPane.showMessageDialog(this, "Patient with this ID already exists!",
                        "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert patient
            String query = "INSERT INTO patient_info(ID, Contact, Name, Gender, Age, Patient_Disease, Room_Number, Time, Deposite) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, idType + ": " + idNumber);
            pstmt.setString(2, contact);
            pstmt.setString(3, name);
            pstmt.setString(4, gender);
            pstmt.setInt(5, Integer.parseInt(age));
            pstmt.setString(6, disease);
            pstmt.setString(7, room);
            pstmt.setString(8, time);
            pstmt.setString(9, deposit);
            pstmt.executeUpdate();

            // Update room status
            String updateRoom = "UPDATE Room SET Availability = 'Occupied' WHERE room_no = ?";
            pstmt = c.connection.prepareStatement(updateRoom);
            pstmt.setString(1, room);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Patient registered successfully!");
            clearForm();
            loadAvailableRooms(roomChoice); // Refresh room list

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid age and deposit amount",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean patientExists(conn c, String idNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM patient_info WHERE ID LIKE ?";
        PreparedStatement pstmt = c.connection.prepareStatement(query);
        pstmt.setString(1, "%" + idNumber);
        ResultSet rs = pstmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private boolean validateInputs() {
        if (textFieldID.getText().trim().isEmpty()) {
            showError("Please enter ID number");
            textFieldID.requestFocus();
            return false;
        }
        if (textFieldContact.getText().trim().isEmpty() || textFieldContact.getText().trim().length() != 10) {
            showError("Please enter valid 10-digit contact number");
            textFieldContact.requestFocus();
            return false;
        }
        if (textName.getText().trim().isEmpty()) {
            showError("Please enter patient name");
            textName.requestFocus();
            return false;
        }
        if (!r1.isSelected() && !r2.isSelected()) {
            showError("Please select gender");
            return false;
        }
        if (textFieldAge.getText().trim().isEmpty()) {
            showError("Please enter age");
            textFieldAge.requestFocus();
            return false;
        }
        if (textFieldDisease.getText().trim().isEmpty()) {
            showError("Please enter disease");
            textFieldDisease.requestFocus();
            return false;
        }
        if (textFieldDeposite.getText().trim().isEmpty()) {
            showError("Please enter deposit amount");
            textFieldDeposite.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        textFieldID.setText("");
        textFieldContact.setText("");
        textName.setText("");
        textFieldAge.setText("");
        textFieldDisease.setText("");
        textFieldDeposite.setText("");
        idTypeCombo.setSelectedIndex(0);
        ButtonGroup group = new ButtonGroup();
        group.clearSelection();
        updateDateTime();
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
        SwingUtilities.invokeLater(NEW_PATIENT::new);
    }
}