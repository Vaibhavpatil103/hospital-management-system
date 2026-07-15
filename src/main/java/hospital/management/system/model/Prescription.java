package hospital.management.system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Prescription {
    private int prescriptionId;
    private int patientId;
    private int doctorId;
    private String medicineName;
    private String dosage;
    private String duration;
    private String notes;
    private LocalDate prescribedDate;
    private LocalDateTime createdAt;

    public Prescription() {}

    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getPrescribedDate() { return prescribedDate; }
    public void setPrescribedDate(LocalDate prescribedDate) { this.prescribedDate = prescribedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
