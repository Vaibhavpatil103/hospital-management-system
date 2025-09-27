package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.border.EmptyBorder;

public class Reception extends JFrame {

    // Color scheme
    private static final Color PRIMARY_BLUE = new Color(0, 112, 192);
    private static final Color DARK_BLUE = new Color(0, 82, 162);
    private static final Color LIGHT_GRAY = new Color(245, 247, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color DARK_TEXT = new Color(51, 51, 51);
    private static final Color LIGHT_TEXT = new Color(119, 119, 119);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 18);
    private static final Font CARD_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public Reception() {
        setTitle("Hospital Management System - Reception Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(mainPanel);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(3, 3, 25, 25));
        cardsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        cardsPanel.setBackground(LIGHT_GRAY);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        String[][] cardData = {
                {"Add New Patient", "Register new patients", "patient-icon.png"},
                {"Employee Info", "View staff details", "staff-icon.png"},
                {"Update Patient", "Modify patient records", "update-icon.png"},
                {"Room Management", "Manage hospital rooms", "room-icon.png"},
                {"Patient Info", "View all patients", "patients-icon.png"},
                {"Ambulance", "Manage ambulance services", "ambulance-icon.png"},
                {"Departments", "View department info", "dept-icon.png"},
                {"Patient Discharge", "Process discharges", "discharge-icon.png"},
                {"Search Room", "Find available rooms", "search-icon.png"}
        };

        ActionListener[] actions = {
                e -> new NEW_PATIENT(), e -> new EmployeeInfo(), e -> new UpdatePatientDetails(),
                e -> new Room(), e -> new ALL_Patient_Info(), e -> new Ambulance(),
                e -> new Department(), e -> new PatientDischarge(), e -> new SearchRoom()
        };

        for (int i = 0; i < cardData.length; i++) {
            cardsPanel.add(createDashboardCard(
                    cardData[i][0],
                    cardData[i][1],
                    cardData[i][2],
                    actions[i]
            ));
        }

        // Add logout button to footer panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(LIGHT_GRAY);

        JLabel footerLabel = new JLabel("LAST MOMENT HOSPITAL", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        footerLabel.setForeground(DARK_BLUE);
        footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        footerPanel.add(footerLabel, BorderLayout.CENTER);

        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBackground(PRIMARY_BLUE);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Close current window
                new Login(); // Assuming you have a Login class
            }
        });

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        footerPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Reception Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icon/logo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));

            logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showAboutDialog();
                }

                public void mouseEntered(MouseEvent e) {
                    logoLabel.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
                }

                public void mouseExited(MouseEvent e) {
                    logoLabel.setBorder(null);
                }
            });

            headerPanel.add(logoLabel, BorderLayout.EAST);
        } catch (Exception e) {
            JLabel textLogo = new JLabel("LAST MOMENT HOSPITAL");
            textLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            textLogo.setForeground(PRIMARY_BLUE);
            headerPanel.add(textLogo, BorderLayout.EAST);
        }

        return headerPanel;
    }

    private JPanel createDashboardCard(String title, String description, String iconPath, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(CARD_DESC_FONT);
        descLabel.setForeground(LIGHT_TEXT);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(CARD_WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon/" + iconPath));
            if (icon.getImage() != null) {
                Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
                iconLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
                textPanel.add(iconLabel, BorderLayout.EAST);
            }
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        card.add(textPanel, BorderLayout.CENTER);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }

            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
        });

        return card;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "<html><center><b>LAST MOMENT HOSPITAL</b><br>" +
                        "Hospital management software<br><br>" +
                        "Vaibhav Patil<br>" +
                        "© SCOE, Kharghar, Navi Mumbai</center></html>",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
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

        SwingUtilities.invokeLater(() -> new Reception());
    }
}