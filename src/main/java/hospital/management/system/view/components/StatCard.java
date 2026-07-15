package hospital.management.system.view.components;

import hospital.management.system.util.AppTheme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {

    public StatCard(String title, String value, String iconPath) {
        setLayout(new net.miginfocom.swing.MigLayout("fill, insets 20", "[][grow]", "[][]"));
        setBackground(AppTheme.CARD_BACKGROUND);
        setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);
        
        // Use a placeholder if icon is missing
        JLabel iconLabel = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource(iconPath);
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }

        add(iconLabel, "spany 2, align left, gapright 15");
        add(titleLabel, "wrap");
        add(valueLabel, "");
    }
}
