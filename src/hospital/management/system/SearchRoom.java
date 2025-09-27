package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import com.formdev.flatlaf.FlatLightLaf;

public class SearchRoom extends JFrame {

    private JComboBox<String> statusComboBox;
    private JTable roomTable;
    private static final Color PRIMARY_COLOR = new Color(0, 112, 192);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    public SearchRoom() {
        // Frame setup
        setTitle("LAST MOMENT HOSPITAL - Room Search");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Search For Room");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Status filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setBackground(BACKGROUND_COLOR);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);
        filterPanel.add(statusLabel);

        statusComboBox = new JComboBox<>(new String[]{"Available", "Occupied"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(new Dimension(120, 30));
        filterPanel.add(statusComboBox);

        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> searchRooms());
        filterPanel.add(searchButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Table setup
        roomTable = new JTable();
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomTable.setRowHeight(30);
        roomTable.setSelectionBackground(new Color(52, 152, 219, 100));

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Table header styling
        JTableHeader header = roomTable.getTableHeader();
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

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton backButton = createStyledButton("Back", new Color(128, 128, 128));
        backButton.addActionListener(e -> {
            this.dispose();
            new Reception();
        });
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadRoomData();

        setVisible(true);
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

    private void loadRoomData() {
        try {
            conn c = new conn();
            String query = "SELECT room_no AS 'Room Number', " +
                    "Room_type AS 'Room Type', " +
                    "Price, " +
                    "Availability " +
                    "FROM Room"; // Corrected table name
            ResultSet rs = c.statement.executeQuery(query);
            roomTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            showErrorDialog("Failed to load room data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchRooms() {
        String status = (String) statusComboBox.getSelectedItem();
        try {
            conn c = new conn();
            String query = "SELECT room_no AS 'Room Number', " +
                    "Room_type AS 'Room Type', " +
                    "Price, " +
                    "Availability " +
                    "FROM Room " + // Corrected table name
                    "WHERE Availability = '" + status + "'";
            ResultSet rs = c.statement.executeQuery(query);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this,
                        "No rooms found with status: " + status,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            roomTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            showErrorDialog("Failed to search rooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new SearchRoom());
    }
}