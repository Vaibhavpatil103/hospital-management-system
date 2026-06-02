package hospital.management.system.view;

import hospital.management.system.service.AuthService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.SessionManager;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class DashboardView extends BaseFrame {

    public DashboardView() {
        super("Dashboard", 1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout());

        // Header image
        try {
            URL imgUrl = getClass().getResource("/icon/dr.png");
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(960, 250, Image.SCALE_SMOOTH);
                JLabel banner = new JLabel(new ImageIcon(img));
                banner.setBorder(new EmptyBorder(0, 0, 20, 0));
                contentPanel.add(banner, BorderLayout.NORTH);
            }
        } catch (Exception e) {
            System.err.println("Banner image not found");
        }

        // Grid of modules
        JPanel gridPanel = new JPanel(new GridLayout(3, 4, 15, 15)); // Changed to 4 columns to fit 10 items + logout
        gridPanel.setBackground(AppTheme.BACKGROUND);

        // Add module buttons
        gridPanel.add(createDashboardCard("Add New Patient", () -> {
            new PatientRegistrationView();
        }));
        
        gridPanel.add(createDashboardCard("Patient List", () -> {
            new PatientListView();
        }));
        
        gridPanel.add(createDashboardCard("Update Patient", () -> {
            new PatientUpdateView();
        }));
        
        gridPanel.add(createDashboardCard("Room Information", () -> {
            new RoomView();
        }));
        
        gridPanel.add(createDashboardCard("Search Room", () -> {
            new RoomSearchView();
        }));
        
        gridPanel.add(createDashboardCard("Department", () -> {
            new DepartmentView();
        }));
        
        gridPanel.add(createDashboardCard("Employee Details", () -> {
            new EmployeeView();
        }));
        
        gridPanel.add(createDashboardCard("Ambulance", () -> {
            new AmbulanceView();
        }));
        
        gridPanel.add(createDashboardCard("Discharge Patient", () -> {
            new PatientDischargeView();
        }));
        
        // New Billing Module
        gridPanel.add(createDashboardCard("Billing", () -> {
            new BillingView();
        }));

        // Logout button
        JButton logoutBtn = UIComponentFactory.createDangerButton("Logout", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new AuthService().logout();
                dispose();
                new LoginView();
            }
        });
        
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(AppTheme.BACKGROUND);
        logoutPanel.add(logoutBtn, BorderLayout.CENTER);
        
        gridPanel.add(logoutPanel);

        contentPanel.add(gridPanel, BorderLayout.CENTER);
    }

    private JButton createDashboardCard(String title, Runnable action) {
        JButton button = new JButton(title);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(AppTheme.CARD_BACKGROUND);
        button.setForeground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppTheme.PRIMARY);
                button.setForeground(AppTheme.TEXT_ON_PRIMARY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppTheme.CARD_BACKGROUND);
                button.setForeground(AppTheme.PRIMARY);
            }
        });

        button.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                showError("Error opening module: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return button;
    }
}
