package hospital.management.system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LabTest {
    private int testId;
    private int patientId;
    private Integer doctorId;
    private String testName;
    private String testResult;
    private LocalDate testDate;
    private String status;
    private LocalDateTime createdAt;

    public LabTest() {}

    public int getTestId() { return testId; }
    public void setTestId(int testId) { this.testId = testId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getTestResult() { return testResult; }
    public void setTestResult(String testResult) { this.testResult = testResult; }

    public LocalDate getTestDate() { return testDate; }
    public void setTestDate(LocalDate testDate) { this.testDate = testDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
