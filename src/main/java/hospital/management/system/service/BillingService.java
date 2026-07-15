package hospital.management.system.service;

import hospital.management.system.dao.BillDAO;
import hospital.management.system.dao.PatientDAO;
import hospital.management.system.dao.RoomDAO;
import hospital.management.system.model.Bill;
import hospital.management.system.model.BillStatus;
import hospital.management.system.model.Patient;
import hospital.management.system.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import hospital.management.system.dao.*;
import hospital.management.system.model.*;

public class BillingService {
    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
    
    private final BillDAO billDAO;
    private final PatientDAO patientDAO;
    private final RoomDAO roomDAO;
    private final AppointmentDAO appointmentDAO;
    private final DoctorDAO doctorDAO;
    private final LabTestDAO labTestDAO;
    private final PrescriptionDAO prescriptionDAO;

    public BillingService() {
        this.billDAO = new BillDAO();
        this.patientDAO = new PatientDAO();
        this.roomDAO = new RoomDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.doctorDAO = new DoctorDAO();
        this.labTestDAO = new LabTestDAO();
        this.prescriptionDAO = new PrescriptionDAO();
    }

    /**
     * Generates a final bill for a patient at the time of discharge.
     */
    public Bill generateBill(int patientId) {
        logger.info("Generating aggregated bill for patient ID: {}", patientId);
        
        Optional<Patient> patientOpt = patientDAO.findById(patientId);
        if (patientOpt.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }
        
        Patient patient = patientOpt.get();
        
        // Calculate days stayed
        long daysStayed = Duration.between(patient.getAdmissionTime(), LocalDateTime.now()).toDays();
        if (daysStayed == 0) daysStayed = 1; // Minimum 1 day charge
        
        BigDecimal roomCharges = BigDecimal.ZERO;
        
        if (patient.getRoomId() != null) {
            Optional<Room> roomOpt = roomDAO.findById(patient.getRoomId());
            if (roomOpt.isPresent()) {
                BigDecimal rate = roomOpt.get().getPricePerDay();
                roomCharges = rate.multiply(BigDecimal.valueOf(daysStayed));
            }
        }
        
        // Aggregate Doctor Fees from Appointments
        BigDecimal doctorFees = BigDecimal.ZERO;
        List<hospital.management.system.model.Appointment> appointments = appointmentDAO.findByPatientId(patientId);
        for (hospital.management.system.model.Appointment app : appointments) {
            Optional<hospital.management.system.model.Doctor> doc = doctorDAO.findById(app.getDoctorId());
            if (doc.isPresent() && doc.get().getConsultationFee() != null) {
                doctorFees = doctorFees.add(doc.get().getConsultationFee());
            }
        }
        
        // Aggregate Other Charges (Lab Tests & Pharmacy via Prescriptions)
        BigDecimal otherCharges = BigDecimal.ZERO;
        List<hospital.management.system.model.LabTest> tests = labTestDAO.findByPatientId(patientId);
        // Fixed rate $50 per lab test
        otherCharges = otherCharges.add(new BigDecimal(tests.size() * 50));
        
        List<hospital.management.system.model.Prescription> prescriptions = prescriptionDAO.findByPatientId(patientId);
        // Fixed rate $15 per prescription
        otherCharges = otherCharges.add(new BigDecimal(prescriptions.size() * 15));
        
        BigDecimal total = roomCharges.add(doctorFees).add(otherCharges);
        BigDecimal deposit = patient.getDeposit() != null ? patient.getDeposit() : BigDecimal.ZERO;
        BigDecimal balance = total.subtract(deposit);
        
        Bill bill = new Bill();
        bill.setPatientId(patientId);
        bill.setRoomCharges(roomCharges);
        bill.setDoctorFees(doctorFees);
        bill.setOtherCharges(otherCharges);
        bill.setTotalAmount(total);
        bill.setDepositPaid(deposit);
        bill.setBalanceDue(balance);
        bill.setStatus(balance.compareTo(BigDecimal.ZERO) <= 0 ? BillStatus.PAID : BillStatus.PENDING);
        
        billDAO.save(bill);
        logger.info("Bill generated successfully for patient ID: {}", patientId);
        
        return bill;
    }

    public Optional<Bill> getLatestBillForPatient(int patientId) {
        return billDAO.findByPatientId(patientId);
    }

    public void markBillAsPaid(int billId) {
        billDAO.updateStatus(billId, "PAID");
        logger.info("Bill {} marked as PAID", billId);
    }
}
