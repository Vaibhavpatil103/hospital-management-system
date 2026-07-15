package hospital.management.system.model;

/**
 * Represents the gender of a patient.
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    /**
     * Safely converts a string to Gender, defaulting to OTHER if invalid.
     */
    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
