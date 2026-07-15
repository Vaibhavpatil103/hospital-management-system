package hospital.management.system.service;

import hospital.management.system.dao.DoctorDAO;
import hospital.management.system.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
    private final DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAll();
    }

    public List<Doctor> getActiveDoctors() {
        return doctorDAO.findActiveDoctors();
    }

    public void addDoctor(Doctor doctor) {
        if (doctor.getFullName() == null || doctor.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be empty");
        }
        doctorDAO.save(doctor);
        logger.info("Added new doctor: {}", doctor.getFullName());
    }

    public void updateDoctor(Doctor doctor) {
        doctorDAO.update(doctor);
        logger.info("Updated doctor ID: {}", doctor.getDoctorId());
    }
}
