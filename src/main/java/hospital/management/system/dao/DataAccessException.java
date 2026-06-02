package hospital.management.system.dao;

/**
 * Custom unchecked exception for database operations.
 * Wraps SQLExceptions to avoid checked exception hell across the application.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
