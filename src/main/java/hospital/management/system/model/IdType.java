package hospital.management.system.model;

/**
 * Represents the type of government-issued ID used for patient identification.
 */
public enum IdType {
    AADHAR("Aadhar Card"),
    VOTER_ID("Voter ID"),
    DRIVING_LICENSE("Driving License"),
    PASSPORT("Passport");

    private final String displayName;

    IdType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Safely converts a string to IdType, defaulting to AADHAR if invalid.
     */
    public static IdType fromString(String value) {
        if (value == null || value.isBlank()) {
            return AADHAR;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AADHAR;
        }
    }
}
