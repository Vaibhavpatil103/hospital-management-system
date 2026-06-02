package hospital.management.system.service;

import hospital.management.system.dao.BillDAO;
import hospital.management.system.dao.PatientDAO;
import hospital.management.system.dao.RoomDAO;
import hospital.management.system.model.Bill;
import hospital.management.system.model.Patient;
import hospital.management.system.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BillingService {
    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
    
    private final BillDAO billDAO;
    private final PatientDAO patientDAO;
    private final RoomDAO roomDAO;

    public BillingService() {
        this.billDAO = new BillDAO();
        this.patientDAO = new PatientDAO();
        this.roomDAO = new RoomDAO();
    }

    /**
     * Generates a final bill for a patient at the time of discharge.
     */
    public Bill generateBill(int patientId, BigDecimal doctorFees, BigDecimal otherCharges) {
        logger.info("Generating bill for patient ID: {}", patientId);
        
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
        bill.setStatus(balance.compareTo(BigDecimal.ZERO) <= 0 ? "PAID" : "PENDING");
        
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
