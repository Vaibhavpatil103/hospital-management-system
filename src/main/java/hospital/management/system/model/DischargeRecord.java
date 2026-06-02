package hospital.management.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DischargeRecord {
    private int dischargeId;
    private int patientId;
    private Integer roomId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private BigDecimal deposit;
    private BigDecimal totalBill;
    private Integer dischargedBy; // User ID
    private LocalDateTime createdAt;

    public DischargeRecord() {}

    public int getDischargeId() {
        return dischargeId;
    }

    public void setDischargeId(int dischargeId) {
        this.dischargeId = dischargeId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(BigDecimal totalBill) {
        this.totalBill = totalBill;
    }

    public Integer getDischargedBy() {
        return dischargedBy;
    }

    public void setDischargedBy(Integer dischargedBy) {
        this.dischargedBy = dischargedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
