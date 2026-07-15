package hospital.management.system.view.components;

import hospital.management.system.util.AppTheme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class SidebarNavigation extends JPanel {
    private final JPanel contentPanel;
    private final Map<String, JButton> buttons = new HashMap<>();

    public SidebarNavigation(JPanel contentPanel) {
        this.contentPanel = contentPanel;
        setLayout(new MigLayout("wrap 1, fillx, insets 10 10 10 10", "[grow]", "[]20[]"));
        setBackground(AppTheme.PRIMARY);
        setPreferredSize(new Dimension(250, 0));

        // Logo / Title
        JLabel logoLabel = new JLabel("LMH Dashboard", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(AppTheme.TEXT_ON_PRIMARY);
        add(logoLabel, "growx, gapy 20");

        // Menu Items
        addMenuItem("Dashboard", "DashboardPanel", "/icon/search-icon.png"); // No dashboard icon, fallback
        addMenuItem("Patients", "PatientsPanel", "/icon/patient-icon.png");
        addMenuItem("Patient Care", "PatientCarePanel", "/icon/patient-icon.png");
        addMenuItem("Appointments", "AppointmentsPanel", "/icon/patient-icon.png");
        addMenuItem("Doctors", "DoctorsPanel", "/icon/staff-icon.png");
        addMenuItem("Pharmacy", "InventoryPanel", "/icon/dept-icon.png");
        addMenuItem("Employees", "EmployeesPanel", "/icon/staff-icon.png");
        addMenuItem("Rooms", "RoomsPanel", "/icon/room-icon.png");
        addMenuItem("Ambulance", "AmbulancePanel", "/icon/ambulance-icon.png");
        addMenuItem("Billing", "BillingPanel", "/icon/discharge-icon.png");
        addMenuItem("Audit Logs", "AuditLogPanel", "/icon/search-icon.png");

        add(new JSeparator(SwingConstants.HORIZONTAL), "growx, gapy 20");
        
        // Logout
        JButton logoutBtn = createMenuButton("Logout", "/icon/login.png");
        logoutBtn.addActionListener(e -> handleLogout());
        add(logoutBtn, "growx, pushy, aligny bottom");
    }

    private void addMenuItem(String title, String panelName, String iconPath) {
        JButton btn = createMenuButton(title, iconPath);
        btn.addActionListener(e -> {
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, panelName);
            setActiveButton(title);
        });
        buttons.put(title, btn);
        add(btn, "growx, gapy 5");
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(AppTheme.TEXT_ON_PRIMARY);
        btn.setBackground(AppTheme.PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        try {
            java.net.URL imgUrl = getClass().getResource(iconPath);
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(15);
            }
        } catch (Exception ignored) {}

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getName().equals("ACTIVE")) {
                    btn.setBackground(AppTheme.PRIMARY.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getName().equals("ACTIVE")) {
                    btn.setBackground(AppTheme.PRIMARY);
                }
            }
        });

        btn.setName("INACTIVE");
        return btn;
    }

    private void setActiveButton(String activeTitle) {
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(activeTitle)) {
                btn.setBackground(AppTheme.PRIMARY.darker());
                btn.setName("ACTIVE");
            } else {
                btn.setBackground(AppTheme.PRIMARY);
                btn.setName("INACTIVE");
            }
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new hospital.management.system.service.AuthService().logout();
            SwingUtilities.getWindowAncestor(this).dispose();
            new hospital.management.system.view.LoginView();
        }
    }
}
