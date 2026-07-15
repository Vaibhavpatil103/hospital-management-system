package hospital.management.system.service;

import hospital.management.system.dao.AppointmentDAO;
import hospital.management.system.model.Appointment;
import hospital.management.system.model.AppointmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }

    public void scheduleAppointment(Appointment appointment) {
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot schedule an appointment in the past.");
        }
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentDAO.save(appointment);
        logger.info("Scheduled new appointment for patient: {}", appointment.getPatientName());
    }

    public void markAppointmentCompleted(int appointmentId) {
        appointmentDAO.updateStatus(appointmentId, AppointmentStatus.COMPLETED);
        logger.info("Marked appointment ID {} as COMPLETED", appointmentId);
    }
    
    public void cancelAppointment(int appointmentId) {
        appointmentDAO.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
        logger.info("Marked appointment ID {} as CANCELLED", appointmentId);
    }
}
