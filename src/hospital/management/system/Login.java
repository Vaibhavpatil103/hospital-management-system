package hospital.management.system;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener {

    JTextField textField;
    JPasswordField jPasswordField;
    JButton b1, b2;

    Login() {
        setTitle("LAST MOMENT HOSPITAL - Login");
        setSize(600, 500); // Slightly larger for better spacing
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main content panel with modern styling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 247, 250)); // Light gray background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        add(mainPanel);

        // App logo/icon (placeholder)
        JLabel logo = new JLabel(new ImageIcon("/icon/logo.png")); // Add your own icon
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(logo);

        // Title with modern styling
        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(44, 62, 80)); // Dark blue-gray
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(heading);

        // Subtitle
        JLabel subheading = new JLabel("Sign in to continue");
        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheading.setForeground(new Color(127, 140, 141)); // Gray
        subheading.setAlignmentX(Component.CENTER_ALIGNMENT);
        subheading.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(subheading);

        // Username Label
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(44, 62, 80));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(userLabel);

        // Username Field
        textField = new JTextField();
        styleInputField(textField);
        mainPanel.add(textField);

        // Spacer
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(new Color(44, 62, 80));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passLabel);

        // Password Field
        jPasswordField = new JPasswordField();
        styleInputField(jPasswordField);
        mainPanel.add(jPasswordField);

        // Forgot password link
        JLabel forgotPassword = new JLabel("Forgot password?");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPassword.setForeground(new Color(52, 152, 219)); // Blue
        forgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassword.setAlignmentX(Component.RIGHT_ALIGNMENT);
        forgotPassword.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));
        mainPanel.add(forgotPassword);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        b1 = createStyledButton("Login", new Color(46, 204, 113)); // Green
        b1.addActionListener(this);
        buttonPanel.add(b1);

        b2 = createStyledButton("Cancel", new Color(231, 76, 60)); // Red
        b2.addActionListener(this);
        buttonPanel.add(b2);

        mainPanel.add(buttonPanel);

        // Footer text
        JLabel footer = new JLabel(" LAST MOMENT HOSPITAL | VP");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(149, 165, 166));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        mainPanel.add(footer);

        setVisible(true);
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 25, 12, 25),
                BorderFactory.createLineBorder(bgColor.darker(), 0)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                conn c = new conn();
                String user = textField.getText();
                String pass = new String(jPasswordField.getPassword());

                String q = "SELECT * FROM login WHERE ID = '" + user + "' AND PW = '" + pass + "'";
                ResultSet resultSet = c.statement.executeQuery(q);

                if (resultSet.next()) {
                    new Reception();
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.exit(0);
        }
    }
    

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Additional UI improvements
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Login();
    }
}

