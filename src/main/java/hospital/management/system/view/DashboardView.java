package hospital.management.system.view;

import hospital.management.system.view.components.SidebarNavigation;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends BaseFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardView() {
        super("Hospital Management System - Dashboard", 1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        // We replace BaseFrame's default border layout with MigLayout on the contentPanel
        contentPanel.setLayout(new MigLayout("fill, insets 0", "[250!][grow]", "[grow]"));

        // Central Content Area using CardLayout for easy swapping
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        // Add Dashboard Panel
        mainContentPanel.add(new DashboardPanel(), "DashboardPanel");
        
        // Add Placeholder Panels for other modules (To be built in future phases)
        mainContentPanel.add(new PatientListView(), "PatientsPanel");
        mainContentPanel.add(new EmployeeView(), "EmployeesPanel");
        mainContentPanel.add(new RoomView(), "RoomsPanel");
        mainContentPanel.add(new AmbulanceView(), "AmbulancePanel");
        mainContentPanel.add(new BillingView(), "BillingPanel");
        mainContentPanel.add(new DoctorView(), "DoctorsPanel");
        mainContentPanel.add(new AppointmentView(), "AppointmentsPanel");
        mainContentPanel.add(new InventoryView(), "InventoryPanel");
        mainContentPanel.add(new PatientCareView(), "PatientCarePanel");
        mainContentPanel.add(new AuditLogView(), "AuditLogPanel");

        // Add Sidebar
        SidebarNavigation sidebar = new SidebarNavigation(mainContentPanel);
        contentPanel.add(sidebar, "growy");
        contentPanel.add(mainContentPanel, "grow");
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel lbl = new JLabel(title + " - Coming Soon / Use Quick Actions");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(lbl);
        return panel;
    }
}
