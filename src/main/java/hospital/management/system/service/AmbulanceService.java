package hospital.management.system.service;

import hospital.management.system.dao.AmbulanceDAO;
import hospital.management.system.model.Ambulance;
import hospital.management.system.util.InputValidator;
import hospital.management.system.util.InputValidator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service layer for Ambulance business logic.
 */
public class AmbulanceService {
    private static final Logger logger = LoggerFactory.getLogger(AmbulanceService.class);
    private final AmbulanceDAO ambulanceDAO;

    public AmbulanceService() {
        this.ambulanceDAO = new AmbulanceDAO();
    }

    public List<Ambulance> getAllAmbulances() {
        return ambulanceDAO.findAll();
    }

    public void addAmbulance(Ambulance ambulance) {
        logger.info("Adding new ambulance: {}", ambulance.getVehicleName());
        validateAmbulance(ambulance);
        ambulanceDAO.save(ambulance);
        logger.info("Ambulance added successfully with ID: {}", ambulance.getAmbulanceId());
    }

    public void updateAmbulance(Ambulance ambulance) {
        logger.info("Updating ambulance ID: {}", ambulance.getAmbulanceId());
        validateAmbulance(ambulance);
        ambulanceDAO.update(ambulance);
        logger.info("Ambulance updated successfully: {}", ambulance.getAmbulanceId());
    }

    public void deleteAmbulance(int ambulanceId) {
        logger.info("Deleting ambulance ID: {}", ambulanceId);
        ambulanceDAO.delete(ambulanceId);
        logger.info("Ambulance deleted successfully: {}", ambulanceId);
    }

    private void validateAmbulance(Ambulance ambulance) {
        ValidationResult v;

        v = InputValidator.validateRequired(ambulance.getDriverName(), "Driver Name");
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validatePhone(ambulance.getContact());
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validateRequired(ambulance.getVehicleName(), "Vehicle Name");
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());

        v = InputValidator.validateRequired(ambulance.getLocation(), "Location");
        if (!v.isValid()) throw new IllegalStateException(v.getErrorMessage());
    }
}
