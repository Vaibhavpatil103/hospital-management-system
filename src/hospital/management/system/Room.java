package hospital.management.system;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;

import com.formdev.flatlaf.FlatLightLaf;

public class Room extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TABLE_HEADER_COLOR = new Color(0, 112, 192);
    private static final Color TABLE_TEXT_COLOR = new Color(33, 33, 33);

    public Room() {
        setTitle("LAST MOMENT HOSPITAL - Room Info");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Title label
        JLabel titleLabel = new JLabel("Room Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Room Image (Icon)
        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/roomm.png"));
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(image);
        JLabel imageLabel = new JLabel(scaledIcon);
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(BACKGROUND_COLOR);
        imagePanel.add(imageLabel);
        mainPanel.add(imagePanel, BorderLayout.EAST);

        // Table
        JTable table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));
        table.setForeground(TABLE_TEXT_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load data into the table
        try {
            conn c = new conn();
            String q = "SELECT * FROM room";
            ResultSet resultSet = c.statement.executeQuery(q);
            table.setModel(DbUtils.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading room data", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Button panel for back button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(new Color(231, 76, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        backButton.addActionListener((ActionEvent e) -> setVisible(false));
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
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

        SwingUtilities.invokeLater(Room::new);
    }
}
