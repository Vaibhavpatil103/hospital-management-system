package hospital.management.system.util;

import java.awt.*;

/**
 * Centralized design tokens for the entire application.
 * Single source of truth for colors, fonts, and dimensions.
 */
public final class AppTheme {

    private AppTheme() {}

    // ── Primary Colors ──────────────────────────────────────
    public static final Color PRIMARY = new Color(0, 112, 192);
    public static final Color PRIMARY_DARK = new Color(0, 82, 162);
    public static final Color PRIMARY_LIGHT = new Color(52, 152, 219);

    // ── Semantic Colors ─────────────────────────────────────
    public static final Color SUCCESS = new Color(39, 174, 96);
    public static final Color DANGER = new Color(231, 76, 60);
    public static final Color WARNING = new Color(241, 196, 15);
    public static final Color INFO = new Color(52, 152, 219);

    // ── Background Colors ───────────────────────────────────
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TABLE_HEADER_BG = new Color(0, 112, 192);

    // ── Text Colors ─────────────────────────────────────────
    public static final Color TEXT_PRIMARY = new Color(51, 51, 51);
    public static final Color TEXT_SECONDARY = new Color(119, 119, 119);
    public static final Color TEXT_LIGHT = new Color(149, 165, 166);
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    // ── Border Colors ───────────────────────────────────────
    public static final Color BORDER = new Color(220, 220, 220);
    public static final Color BORDER_LIGHT = new Color(230, 230, 230);

    // ── Selection Colors ────────────────────────────────────
    public static final Color SELECTION_BG = new Color(52, 152, 219, 100);
    public static final Color SELECTION_FG = Color.WHITE;

    // ── Fonts ───────────────────────────────────────────────
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font CARD_TITLE_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 18);
    public static final Font CARD_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FOOTER_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Dimensions ──────────────────────────────────────────
    public static final int TABLE_ROW_HEIGHT = 30;
    public static final int BUTTON_PADDING_V = 10;
    public static final int BUTTON_PADDING_H = 25;
    public static final int PANEL_PADDING = 20;
    public static final int CARD_PADDING = 25;
    public static final int FIELD_HEIGHT = 35;

    // ── Application Info ────────────────────────────────────
    public static final String APP_NAME = "LAST MOMENT HOSPITAL";
    public static final String APP_SUBTITLE = "Hospital Management System";
    public static final String APP_FOOTER = "LAST MOMENT HOSPITAL | VP";
    public static final String APP_VERSION = "2.0.0";
}
