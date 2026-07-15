package hospital.management.system.service;

import hospital.management.system.dao.AuditLogDAO;
import hospital.management.system.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogDAO auditLogDAO;

    public AuditService() {
        this.auditLogDAO = new AuditLogDAO();
    }

    public void logEvent(String action, String details, Integer userId) {
        try {
            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setDetails(details);
            log.setUserId(userId);
            auditLogDAO.save(log);
            logger.info("Audit Log [{}] saved for User ID: {}", action, userId);
        } catch (Exception e) {
            logger.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    public List<AuditLog> getAllLogs() {
        return auditLogDAO.findAll();
    }
}
