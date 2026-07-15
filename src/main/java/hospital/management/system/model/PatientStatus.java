package hospital.management.system.model;

/**
 * Represents the status of a patient in the hospital system.
 */
public enum PatientStatus {
    ADMITTED,
    DISCHARGED;

    /**
     * Safely converts a string to PatientStatus, defaulting to ADMITTED if invalid.
     */
    public static PatientStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ADMITTED;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ADMITTED;
        }
    }
}
