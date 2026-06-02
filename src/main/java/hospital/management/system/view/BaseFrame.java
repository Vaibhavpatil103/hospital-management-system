package hospital.management.system.view;

import hospital.management.system.util.AppTheme;
import hospital.management.system.util.SessionManager;
import hospital.management.system.model.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * Base template frame for all application windows.
 * Handles common setup like layout, headers, footers, and authorization.
 */
public abstract class BaseFrame extends JFrame {

    protected JPanel mainPanel;
    protected JPanel contentPanel;
    protected JPanel headerPanel;

    public BaseFrame(String title, int width, int height) {
        // Setup basic frame
        setTitle(AppTheme.APP_NAME + " - " + title);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Setup icon
        try {
            URL iconUrl = getClass().getResource("/icon/logo.png");
            if (iconUrl != null) {
                setIconImage(new ImageIcon(iconUrl).getImage());
            }
        } catch (Exception e) {
            // Ignore if icon not found
        }

        // Setup main container
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppTheme.BACKGROUND);
        setContentPane(mainPanel);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppTheme.BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add user info if logged in
        if (SessionManager.isLoggedIn()) {
            JLabel userLabel = new JLabel("User: " + SessionManager.getCurrentUser().getUsername() + 
                                       " [" + SessionManager.getCurrentUser().getRole() + "]");
            userLabel.setFont(AppTheme.SMALL_FONT);
            userLabel.setForeground(AppTheme.TEXT_SECONDARY);
            headerPanel.add(userLabel, BorderLayout.EAST);
        }

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel for subclasses to populate
        contentPanel = new JPanel();
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(AppTheme.BACKGROUND);
        footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel footerLabel = new JLabel(AppTheme.APP_FOOTER);
        footerLabel.setFont(AppTheme.FOOTER_FONT);
        footerLabel.setForeground(AppTheme.TEXT_LIGHT);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows error dialog
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows success dialog
     */
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows warning dialog
     */
    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Helper to require specific role to open this frame
     */
    protected boolean requireRole(Role role) {
        if (!SessionManager.hasRole(role)) {
            showError("Access Denied. You do not have permission to view this.");
            dispose();
            return false;
        }
        return true;
    }
}
