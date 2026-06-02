package hospital.management.system.view;

import hospital.management.system.service.AuthService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;

public class LoginView extends BaseFrame {

    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 5;

    public LoginView() {
        super("Login", 750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app if login is closed
        
        this.authService = new AuthService();
        
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout(20, 0));

        // Left side: Image
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/icon/login.png"));
            Image image = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            contentPanel.add(imageLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Login image not found");
        }

        // Right side: Form
        JPanel formPanel = UIComponentFactory.createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = UIComponentFactory.createHeaderLabel("Sign In to HMS");
        formPanel.add(titleLabel, gbc);

        // Username
        gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(UIComponentFactory.createLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = UIComponentFactory.createTextField();
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIComponentFactory.createLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = UIComponentFactory.createPasswordField();
        formPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppTheme.BACKGROUND);

        JButton loginBtn = UIComponentFactory.createPrimaryButton("Login", e -> handleLogin());
        JButton cancelBtn = UIComponentFactory.createDangerButton("Cancel", e -> System.exit(0));

        // Add Enter key support
        getRootPane().setDefaultButton(loginBtn);

        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showWarning("Please enter both username and password.");
            return;
        }

        if (attemptCount >= MAX_ATTEMPTS) {
            showError("Maximum login attempts exceeded. Please try again later.");
            return;
        }

        // Run authentication in background thread to prevent UI freezing
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authService.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        dispose(); // Close login window
                        new DashboardView(); // Open dashboard
                    } else {
                        attemptCount++;
                        showError("Invalid username or password. Attempts remaining: " + (MAX_ATTEMPTS - attemptCount));
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("An error occurred during login: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
