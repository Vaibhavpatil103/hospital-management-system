package hospital.management.system.util;

import hospital.management.system.model.Role;
import hospital.management.system.model.User;

/**
 * Manages the currently authenticated user's session.
 */
public final class SessionManager {

    private static User currentUser;
    private static long lastActivityTime = 0;
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes

    private SessionManager() {}

    /**
     * Start a session for the given user.
     */
    public static void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        currentUser = user;
        updateActivity();
    }

    /**
     * End the current session.
     */
    public static void logout() {
        currentUser = null;
        lastActivityTime = 0;
    }

    /**
     * Updates the last activity time for the session.
     */
    public static void updateActivity() {
        if (currentUser != null) {
            lastActivityTime = System.currentTimeMillis();
        }
    }

    /**
     * Checks if a session has expired.
     */
    private static void checkTimeout() {
        if (currentUser != null && (System.currentTimeMillis() - lastActivityTime) > SESSION_TIMEOUT_MS) {
            logout();
        }
    }

    /**
     * Gets the currently logged in user.
     * @return User object or null if not logged in.
     */
    public static User getCurrentUser() {
        checkTimeout();
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public static boolean isLoggedIn() {
        checkTimeout();
        return currentUser != null;
    }

    /**
     * Checks if the currently logged in user has the specified role.
     */
    public static boolean hasRole(Role role) {
        checkTimeout();
        if (!isLoggedIn() || role == null) {
            return false;
        }
        return currentUser.getRole() == role;
    }

    /**
     * Helper to assert that a user has a specific role. Throws if they don't.
     */
    public static void requireRole(Role role) {
        if (!hasRole(role)) {
            throw new SecurityException("Access denied. Required role: " + role);
        }
    }
}
