package hospital.management.system;

import com.formdev.flatlaf.FlatLightLaf;
import hospital.management.system.view.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Entry point for the Hospital Management System.
 * Bootstraps the application, sets Look and Feel, and launches the login view.
 */
public class HospitalManagementApp {
    private static final Logger logger = LoggerFactory.getLogger(HospitalManagementApp.class);

    public static void main(String[] args) {
        logger.info("Starting Hospital Management System...");

        // Initialize Database Connection Pool
        try {
            hospital.management.system.config.DatabaseManager.initialize();
        } catch (Exception ex) {
            logger.error("Failed to initialize database", ex);
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + ex.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Setup FlatLaf Look and Feel for modern UI
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Optional: Customize FlatLaf defaults here
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ex) {
            logger.error("Failed to initialize FlatLaf", ex);
        }

        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginView();
            } catch (Exception e) {
                logger.error("Critical error during application startup", e);
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(), 
                    "Critical Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
