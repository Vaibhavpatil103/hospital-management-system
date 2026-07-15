package hospital.management.system.util;

import hospital.management.system.config.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class DataSeeder {
    
    private static final String[] FIRST_NAMES = {"Aarav", "Vivaan", "Aditya", "Vihaan", "Arjun", "Sai", "Ayaan", "Krishna", "Ishaan", "Shaurya", "Saanvi", "Aanya", "Aadhya", "Aaradhya", "Ananya", "Pari", "Anika", "Navya", "Angel", "Diya", "Rahul", "Priya", "Amit", "Sneha", "Karan", "Pooja", "Vikram", "Neha", "Rohan", "Kavya"};
    private static final String[] LAST_NAMES = {"Sharma", "Patel", "Singh", "Kumar", "Das", "Kaur", "Ram", "Yadav", "Kumari", "Lal", "Gupta", "Choudhary", "Mishra", "Pandey", "Joshi", "Nair", "Reddy", "Rao", "Verma", "Tiwari"};
    private static final String[] DISEASES = {"Fever", "Cough", "Headache", "Malaria", "Dengue", "Typhoid", "Covid-19", "Fracture", "Food Poisoning", "Migraine", "Diabetes", "Hypertension", "Asthma", "Allergy", "Viral Infection"};
    private static final String[] STATUSES = {"ADMITTED", "DISCHARGED"};
    
    public static void main(String[] args) {
        try {
            DatabaseManager.initialize();
            seedData();
            System.out.println("Data Seeding Complete!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.shutdown();
        }
    }

    private static void seedData() throws Exception {
        Random rand = new Random();
        int entriesToGenerate = 50;

        try (Connection conn = DatabaseManager.getConnection()) {
            
            // Seed Patients
            String insertPatient = "INSERT INTO patients (id_type, id_number, contact, full_name, gender, age, disease, room_id, deposit, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertPatient, Statement.RETURN_GENERATED_KEYS)) {
                
                for (int i = 0; i < entriesToGenerate; i++) {
                    String fullName = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
                    String idType = rand.nextBoolean() ? "AADHAR" : "PAN";
                    if(idType.equals("PAN")) idType = "VOTER_ID"; // Enum mapping
                    String idNum = String.format("%012d", rand.nextInt(1000000000) + rand.nextInt(100000));
                    String contact = "9" + String.format("%09d", rand.nextInt(1000000000));
                    String gender = rand.nextBoolean() ? "MALE" : "FEMALE";
                    int age = rand.nextInt(70) + 10;
                    String disease = DISEASES[rand.nextInt(DISEASES.length)];
                    
                    String status = rand.nextDouble() > 0.3 ? "DISCHARGED" : "ADMITTED";
                    Integer roomId = status.equals("ADMITTED") ? (rand.nextInt(15) + 1) : null;
                    double deposit = status.equals("ADMITTED") ? 5000.0 : 0.0;
                    
                    // Random date in the last 30 days
                    LocalDate date = LocalDate.now().minusDays(rand.nextInt(30));
                    
                    pstmt.setString(1, idType);
                    pstmt.setString(2, idNum);
                    pstmt.setString(3, contact);
                    pstmt.setString(4, fullName);
                    pstmt.setString(5, gender);
                    pstmt.setInt(6, age);
                    pstmt.setString(7, disease);
                    if (roomId != null) {
                        pstmt.setInt(8, roomId);
                    } else {
                        pstmt.setNull(8, java.sql.Types.INTEGER);
                    }
                    pstmt.setDouble(9, deposit);
                    pstmt.setString(10, status);
                    pstmt.setDate(11, java.sql.Date.valueOf(date));
                    
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Fetch created patients
            int minPatientId = 1;
            int maxPatientId = 1;
            try(Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT MIN(patient_id), MAX(patient_id) FROM patients")) {
                if(rs.next()){
                    minPatientId = rs.getInt(1);
                    maxPatientId = rs.getInt(2);
                }
            }

            // Seed Appointments
            String insertAppt = "INSERT INTO appointments (patient_id, patient_name, patient_phone, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAppt)) {
                for (int i = 0; i < entriesToGenerate; i++) {
                    int pId = rand.nextInt(maxPatientId - minPatientId + 1) + minPatientId;
                    int docId = rand.nextInt(3) + 1; // Doctors 1,2,3
                    
                    // Spread appointments over last 15 days and next 5 days
                    LocalDate date = LocalDate.now().minusDays(15).plusDays(rand.nextInt(20));
                    LocalTime time = LocalTime.of(9 + rand.nextInt(8), (rand.nextInt(4) * 15)); // 9 AM to 5 PM
                    
                    String status = date.isBefore(LocalDate.now()) ? "COMPLETED" : "SCHEDULED";
                    
                    pstmt.setInt(1, pId);
                    pstmt.setString(2, "Patient " + pId);
                    pstmt.setString(3, "9999999999");
                    pstmt.setInt(4, docId);
                    pstmt.setDate(5, java.sql.Date.valueOf(date));
                    pstmt.setTime(6, java.sql.Time.valueOf(time));
                    pstmt.setString(7, status);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Seed Bills
            String insertBill = "INSERT INTO bills (patient_id, room_charges, doctor_fees, other_charges, total_amount, deposit_paid, balance_due, bill_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBill)) {
                for (int i = 0; i < entriesToGenerate; i++) {
                    int pId = rand.nextInt(maxPatientId - minPatientId + 1) + minPatientId;
                    
                    double roomChg = rand.nextInt(5) * 1000.0;
                    double docFees = rand.nextInt(3) * 800.0;
                    double other = rand.nextInt(1000);
                    double total = roomChg + docFees + other;
                    
                    String status = rand.nextBoolean() ? "PAID" : "PENDING";
                    double deposit = status.equals("PAID") ? total : (total > 2000 ? 2000 : 0);
                    double bal = total - deposit;

                    LocalDate date = LocalDate.now().minusDays(rand.nextInt(30));
                    
                    pstmt.setInt(1, pId);
                    pstmt.setDouble(2, roomChg);
                    pstmt.setDouble(3, docFees);
                    pstmt.setDouble(4, other);
                    pstmt.setDouble(5, total);
                    pstmt.setDouble(6, deposit);
                    pstmt.setDouble(7, bal);
                    pstmt.setDate(8, java.sql.Date.valueOf(date));
                    pstmt.setString(9, status);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }
}
