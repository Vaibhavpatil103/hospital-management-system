package hospital.management.system.util;

import java.util.regex.Pattern;

/**
 * Centralized input validation utility.
 */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");

    private InputValidator() {}

    /**
     * Holds the result of a validation check.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.failure(fieldName + " is required.");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validatePhone(String phone) {
        ValidationResult required = validateRequired(phone, "Phone Number");
        if (!required.isValid()) return required;

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return ValidationResult.failure("Phone Number must be exactly 10 digits.");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.success(); // Optional field
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return ValidationResult.failure("Invalid email format.");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validateAadhar(String aadhar) {
        ValidationResult required = validateRequired(aadhar, "Aadhar Number");
        if (!required.isValid()) return required;

        if (!AADHAR_PATTERN.matcher(aadhar.trim()).matches()) {
            return ValidationResult.failure("Aadhar Number must be exactly 12 digits.");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validateAge(String ageStr) {
        ValidationResult required = validateRequired(ageStr, "Age");
        if (!required.isValid()) return required;

        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 150) {
                return ValidationResult.failure("Age must be between 0 and 150.");
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.failure("Age must be a valid number.");
        }
    }

    public static ValidationResult validateEmployeeAge(String ageStr) {
        ValidationResult required = validateRequired(ageStr, "Age");
        if (!required.isValid()) return required;

        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 18 || age > 70) {
                return ValidationResult.failure("Employee age must be between 18 and 70.");
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.failure("Age must be a valid number.");
        }
    }

    public static ValidationResult validatePositiveAmount(String amountStr, String fieldName) {
        ValidationResult required = validateRequired(amountStr, fieldName);
        if (!required.isValid()) return required;

        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount < 0) {
                return ValidationResult.failure(fieldName + " cannot be negative.");
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.failure(fieldName + " must be a valid number.");
        }
    }
}
