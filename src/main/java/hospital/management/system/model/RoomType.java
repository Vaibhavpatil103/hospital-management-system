package hospital.management.system.model;

/**
 * Represents the type of hospital room.
 */
public enum RoomType {
    GENERAL("General Ward"),
    PRIVATE("Private Room"),
    ICU("Intensive Care Unit");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Safely converts a string to RoomType, defaulting to GENERAL if invalid.
     */
    public static RoomType fromString(String value) {
        if (value == null || value.isBlank()) {
            return GENERAL;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GENERAL;
        }
    }
}
