package hospital.management.system.service;

import hospital.management.system.dao.PatientDAO;
import hospital.management.system.dao.RoomDAO;
import hospital.management.system.model.Patient;
import hospital.management.system.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final PatientDAO patientDAO;
    private final RoomDAO roomDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
        this.roomDAO = new RoomDAO();
    }

    public List<Patient> getAllAdmittedPatients() {
        return patientDAO.findAdmitted();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    public void registerPatient(Patient patient) {
        logger.info("Registering new patient: {}", patient.getFullName());
        
        // 1. Business validation: duplicate check
        if (patientDAO.exists(patient.getIdType(), patient.getIdNumber())) {
            throw new IllegalStateException("A patient with this ID already exists in the system.");
        }

        // 2. Validate room availability
        if (patient.getRoomId() != null) {
            Optional<Room> roomOpt = roomDAO.findById(patient.getRoomId());
            if (roomOpt.isEmpty() || !roomOpt.get().isAvailable()) {
                throw new IllegalStateException("Selected room is not available.");
            }
        }

        // 3. Save patient and update room (Normally we'd use a transaction here, 
        // but since we aren't using Spring/Hibernate, we'll just do it sequentially for simplicity.
        // A true enterprise app would manage the transaction context.)
        patientDAO.save(patient);
        
        if (patient.getRoomId() != null) {
            roomDAO.updateAvailability(patient.getRoomId(), false);
        }
        
        logger.info("Patient registered successfully with ID: {}", patient.getPatientId());
    }

    public void updatePatient(Patient patient, Integer oldRoomId) {
        logger.info("Updating patient: {}", patient.getPatientId());

        // Validate room change
        if (patient.getRoomId() != null && !patient.getRoomId().equals(oldRoomId)) {
            Optional<Room> newRoomOpt = roomDAO.findById(patient.getRoomId());
            if (newRoomOpt.isEmpty() || !newRoomOpt.get().isAvailable()) {
                throw new IllegalStateException("Selected new room is not available.");
            }
            
            // Mark new room as occupied
            roomDAO.updateAvailability(patient.getRoomId(), false);
            
            // Mark old room as available
            if (oldRoomId != null) {
                roomDAO.updateAvailability(oldRoomId, true);
            }
        }

        patientDAO.update(patient);
        logger.info("Patient updated successfully: {}", patient.getPatientId());
    }
}
