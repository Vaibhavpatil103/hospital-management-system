package hospital.management.system.service;

import hospital.management.system.dao.LabTestDAO;
import hospital.management.system.dao.MedicalHistoryDAO;
import hospital.management.system.dao.PrescriptionDAO;
import hospital.management.system.model.LabTest;
import hospital.management.system.model.MedicalHistory;
import hospital.management.system.model.Prescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PatientCareService {
    private static final Logger logger = LoggerFactory.getLogger(PatientCareService.class);
    private final MedicalHistoryDAO medicalHistoryDAO;
    private final PrescriptionDAO prescriptionDAO;
    private final LabTestDAO labTestDAO;

    public PatientCareService() {
        this.medicalHistoryDAO = new MedicalHistoryDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        this.labTestDAO = new LabTestDAO();
    }

    public List<MedicalHistory> getMedicalHistory(int patientId) {
        return medicalHistoryDAO.findByPatientId(patientId);
    }
    public void addMedicalHistory(MedicalHistory history) {
        medicalHistoryDAO.save(history);
        logger.info("Added medical history for patient ID: {}", history.getPatientId());
    }

    public List<Prescription> getPrescriptions(int patientId) {
        return prescriptionDAO.findByPatientId(patientId);
    }
    public void addPrescription(Prescription p) {
        prescriptionDAO.save(p);
        logger.info("Added prescription for patient ID: {}", p.getPatientId());
    }

    public List<LabTest> getLabTests(int patientId) {
        return labTestDAO.findByPatientId(patientId);
    }
    public void orderLabTest(LabTest test) {
        test.setStatus("PENDING");
        labTestDAO.save(test);
        logger.info("Ordered lab test for patient ID: {}", test.getPatientId());
    }
    public void updateLabTestResult(int testId, String result) {
        labTestDAO.updateResult(testId, result, "COMPLETED");
        logger.info("Updated lab test ID: {} to COMPLETED", testId);
    }
}
