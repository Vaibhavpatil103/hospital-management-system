package hospital.management.system.model;

/**
 * Represents the payment status of a bill.
 */
public enum BillStatus {
    PENDING,
    PAID,
    PARTIAL;

    /**
     * Safely converts a string to BillStatus, defaulting to PENDING if invalid.
     */
    public static BillStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
