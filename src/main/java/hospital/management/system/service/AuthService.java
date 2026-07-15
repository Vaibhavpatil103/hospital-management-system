package hospital.management.system.service;

import hospital.management.system.dao.UserDAO;
import hospital.management.system.model.Role;
import hospital.management.system.model.User;
import hospital.management.system.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;
    private final AuditService auditService;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.auditService = new AuditService();
    }

    /**
     * Authenticates a user.
     * @param username the username
     * @param password the plaintext password
     * @return true if successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        logger.info("Authentication attempt for user: {}", username);
        
        Optional<User> optionalUser = userDAO.findByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify BCrypt hash, with fallback for legacy plaintext passwords
            boolean isAuthenticated = false;
            try {
                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    isAuthenticated = true;
                }
            } catch (IllegalArgumentException e) {
                // This happens if the stored hash is not a valid BCrypt hash (e.g. legacy plaintext)
                logger.warn("Encountered legacy plaintext password for user: {}. Attempting seamless migration.", username);
                if (password.equals(user.getPasswordHash())) {
                    // Password matches legacy plaintext. Hash it and update DB.
                    String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
                    userDAO.updatePasswordHash(user.getUserId(), newHash);
                    logger.info("Successfully migrated legacy password to BCrypt for user: {}", username);
                    isAuthenticated = true;
                }
            }

            if (isAuthenticated) {
                logger.info("Authentication successful for user: {}", username);
                userDAO.updateLastLogin(user.getUserId());
                auditService.logEvent("LOGIN", "User logged in successfully", user.getUserId());
                SessionManager.login(user);
                return true;
            } else {
                logger.warn("Authentication failed (wrong password) for user: {}", username);
            }
        } else {
            logger.warn("Authentication failed (user not found or inactive) for user: {}", username);
        }
        
        return false;
    }

    public void logout() {
        if (SessionManager.isLoggedIn()) {
            logger.info("User logged out: {}", SessionManager.getCurrentUser().getUsername());
            SessionManager.logout();
        }
    }
}
