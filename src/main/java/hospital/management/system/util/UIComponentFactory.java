package hospital.management.system.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Factory for creating consistently styled UI components.
 * Eliminates the 6+ copies of createStyledButton scattered across the codebase.
 */
public final class UIComponentFactory {

    private UIComponentFactory() {}

    // ── Buttons ─────────────────────────────────────────────

    public static JButton createPrimaryButton(String text, ActionListener action) {
        return createButton(text, AppTheme.PRIMARY, action);
    }

    public static JButton createSuccessButton(String text, ActionListener action) {
        return createButton(text, AppTheme.SUCCESS, action);
    }

    public static JButton createDangerButton(String text, ActionListener action) {
        return createButton(text, AppTheme.DANGER, action);
    }

    public static JButton createWarningButton(String text, ActionListener action) {
        return createButton(text, AppTheme.WARNING, action);
    }

    public static JButton createSecondaryButton(String text, ActionListener action) {
        return createButton(text, new Color(128, 128, 128), action);
    }

    public static JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(AppTheme.BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(AppTheme.TEXT_ON_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.BUTTON_PADDING_V, AppTheme.BUTTON_PADDING_H,
                AppTheme.BUTTON_PADDING_V, AppTheme.BUTTON_PADDING_H));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        if (action != null) {
            button.addActionListener(action);
        }

        return button;
    }

    // ── Text Fields ─────────────────────────────────────────

    public static JTextField createTextField() {
        return createTextField(20);
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(AppTheme.FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setBackground(Color.WHITE);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(AppTheme.FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setBackground(Color.WHITE);
        return field;
    }

    // ── Labels ──────────────────────────────────────────────

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.LABEL_FONT);
        label.setForeground(AppTheme.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.SUBTITLE_FONT);
        label.setForeground(AppTheme.PRIMARY);
        return label;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.TITLE_FONT);
        label.setForeground(AppTheme.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(AppTheme.PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    // ── Panels ──────────────────────────────────────────────

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppTheme.BACKGROUND);
        return panel;
    }

    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppTheme.BACKGROUND);
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppTheme.CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(
                        AppTheme.CARD_PADDING, AppTheme.CARD_PADDING,
                        AppTheme.CARD_PADDING, AppTheme.CARD_PADDING)));
        return panel;
    }

    public static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(AppTheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        return panel;
    }

    // ── Tables ──────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(AppTheme.TABLE_FONT);
        table.setRowHeight(AppTheme.TABLE_ROW_HEIGHT);
        table.setGridColor(AppTheme.BORDER);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setSelectionBackground(AppTheme.SELECTION_BG);
        table.setSelectionForeground(AppTheme.SELECTION_FG);

        JTableHeader header = table.getTableHeader();
        header.setFont(AppTheme.TABLE_HEADER_FONT);
        header.setBackground(AppTheme.TABLE_HEADER_BG);
        header.setForeground(AppTheme.TEXT_ON_PRIMARY);
        header.setReorderingAllowed(false);
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // ── ComboBox ────────────────────────────────────────────

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(AppTheme.FIELD_FONT);
        comboBox.setPreferredSize(new Dimension(200, AppTheme.FIELD_HEIGHT));
        return comboBox;
    }
}
