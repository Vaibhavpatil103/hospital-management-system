package hospital.management.system;

import com.formdev.flatlaf.FlatLightLaf;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.Statement;

public class Ambulance extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);

    private JTextField nameField, contactField, carNameField, locationField;
    private JComboBox<String> availabilityBox;
    private JTable table;

    public Ambulance() {
        // Frame setup
        setTitle("LAST MOMENT HOSPITAL - Ambulance Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Ambulance Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Input panel (for adding/updating ambulance data)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        contactField = new JTextField();
        carNameField = new JTextField();
        availabilityBox = new JComboBox<>(new String[]{"Available", "Not Available"});
        locationField = new JTextField();

        inputPanel.add(new JLabel("Driver Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Car Name:"));
        inputPanel.add(carNameField);
        inputPanel.add(new JLabel("Availability:"));
        inputPanel.add(availabilityBox);
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);

        mainPanel.add(inputPanel, BorderLayout.WEST);

        // Table setup
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(52, 152, 219, 100));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Table header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Load data
        loadAmbulanceData();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton addButton = createStyledButton("Add", new Color(46, 204, 113));
        addButton.addActionListener(e -> addAmbulance());

        JButton updateButton = createStyledButton("Update", new Color(241, 196, 15));
        updateButton.addActionListener(e -> updateAmbulance());

        JButton backButton = createStyledButton("Back", new Color(231, 76, 60));
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table row selection for updating
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(table.getValueAt(row, 0).toString());
                contactField.setText(table.getValueAt(row, 1).toString());
                carNameField.setText(table.getValueAt(row, 2).toString());
                availabilityBox.setSelectedItem(table.getValueAt(row, 3).toString());
                locationField.setText(table.getValueAt(row, 4).toString());
            }
        });

        setVisible(true);
    }

    private void loadAmbulanceData() {
        try {
            conn c = new conn();
            String query = "SELECT * FROM Ambulance";
            ResultSet rs = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading ambulance data",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addAmbulance() {
        try {
            String name = nameField.getText();
            String contact = contactField.getText();
            String carName = carNameField.getText();
            String availability = (String) availabilityBox.getSelectedItem();
            String location = locationField.getText();

            String query = "INSERT INTO Ambulance (Name, Contact, Car_name, Available, Location) VALUES ('"
                    + name + "', '" + contact + "', '" + carName + "', '" + availability + "', '" + location + "')";

            conn c = new conn();
            c.statement.executeUpdate(query);

            JOptionPane.showMessageDialog(this, "Ambulance Added Successfully!");
            loadAmbulanceData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding ambulance data",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateAmbulance() {
        try {
            int row = table.getSelectedRow();
            if (row != -1) {
                String name = nameField.getText();
                String contact = contactField.getText();
                String carName = carNameField.getText();
                String availability = (String) availabilityBox.getSelectedItem();
                String location = locationField.getText();
                String existingName = table.getValueAt(row, 0).toString();

                String query = "UPDATE Ambulance SET Name = '" + name + "', Contact = '" + contact + "', Car_name = '"
                        + carName + "', Available = '" + availability + "', Location = '" + location
                        + "' WHERE Name = '" + existingName + "'";

                conn c = new conn();
                c.statement.executeUpdate(query);

                JOptionPane.showMessageDialog(this, "Ambulance Updated Successfully!");
                loadAmbulanceData();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an ambulance to update!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating ambulance data",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Ambulance());
    }
}
