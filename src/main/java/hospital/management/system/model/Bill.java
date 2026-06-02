package hospital.management.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int patientId;
    private BigDecimal roomCharges;
    private BigDecimal doctorFees;
    private BigDecimal otherCharges;
    private BigDecimal totalAmount;
    private BigDecimal depositPaid;
    private BigDecimal balanceDue;
    private LocalDateTime billDate;
    private String status; // PENDING, PAID, PARTIAL

    public Bill() {}

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(BigDecimal roomCharges) {
        this.roomCharges = roomCharges;
    }

    public BigDecimal getDoctorFees() {
        return doctorFees;
    }

    public void setDoctorFees(BigDecimal doctorFees) {
        this.doctorFees = doctorFees;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDepositPaid() {
        return depositPaid;
    }

    public void setDepositPaid(BigDecimal depositPaid) {
        this.depositPaid = depositPaid;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
