package hospital.management.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Patient {
    private int patientId;
    private String idType; // Enum string: AADHAR, VOTER_ID, etc.
    private String idNumber;
    private String contact;
    private String fullName;
    private String gender; // MALE, FEMALE, OTHER
    private int age;
    private String disease;
    private Integer roomId; // Can be null if discharged
    private LocalDateTime admissionTime;
    private BigDecimal deposit;
    private String status; // ADMITTED, DISCHARGED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Patient() {}

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(LocalDateTime admissionTime) {
        this.admissionTime = admissionTime;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return patientId == patient.patientId && Objects.equals(idType, patient.idType) && Objects.equals(idNumber, patient.idNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, idType, idNumber);
    }
}
