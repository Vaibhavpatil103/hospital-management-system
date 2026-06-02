package hospital.management.system.util;

import hospital.management.system.model.Role;
import hospital.management.system.model.User;

/**
 * Manages the currently authenticated user's session.
 */
public final class SessionManager {

    private static User currentUser;

    private SessionManager() {}

    /**
     * Start a session for the given user.
     */
    public static void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        currentUser = user;
    }

    /**
     * End the current session.
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * Gets the currently logged in user.
     * @return User object or null if not logged in.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the currently logged in user has the specified role.
     */
    public static boolean hasRole(Role role) {
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
