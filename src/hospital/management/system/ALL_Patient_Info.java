package hospital.management.system;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import com.formdev.flatlaf.FlatLightLaf;

public class ALL_Patient_Info extends JFrame {

    ALL_Patient_Info() {
        setTitle("All Patient Information");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel to hold all UI elements
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 590);
        panel.setBackground(new Color(245, 247, 250)); // Lighter background
        panel.setLayout(null);
        add(panel);

        // Create JTable to display patient info
        JTable table = new JTable();
        table.setBounds(10, 40, 860, 450);
        table.setBackground(new Color(245, 247, 250));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(200, 200, 200)); // Lighter grid color
        table.setSelectionBackground(new Color(0, 112, 192)); // Selection color
        panel.add(table);

        // Load data into the table
        try {
            conn c = new conn();
            String query = "select * from Patient_Info";
            ResultSet resultSet = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create column headers
        createColumnLabel(panel, "ID", 31, 11);
        createColumnLabel(panel, "Number", 150, 11);
        createColumnLabel(panel, "Name", 270, 11);
        createColumnLabel(panel, "Gender", 360, 11);
        createColumnLabel(panel, "Disease", 480, 11);
        createColumnLabel(panel, "Room", 600, 11);
        createColumnLabel(panel, "Time", 700, 11);
        createColumnLabel(panel, "Deposit", 800, 11);

        // Create 'Back' button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(375, 510, 150, 35);
        backButton.setBackground(new Color(0, 112, 192)); // Primary color
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(backButton);

        setUndecorated(true);
        setLayout(null);
        setVisible(true);
    }

    // Helper method for creating column labels
    private void createColumnLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 14);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setForeground(new Color(0, 112, 192)); // Primary color
        panel.add(label);
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
        SwingUtilities.invokeLater(ALL_Patient_Info::new);
    }
}
