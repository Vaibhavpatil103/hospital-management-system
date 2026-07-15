-- ============================================================
-- Hospital Management System v2.0 — Database Schema
-- LAST MOMENT HOSPITAL
-- ============================================================

-- Drop tables in reverse dependency order (if recreating)
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS lab_tests;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS medical_history;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS discharge_records;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS ambulances;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS users;

-- ============================================================
-- Users table (replaces 'login')
-- ============================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST', 'DOCTOR') NOT NULL DEFAULT 'RECEPTIONIST',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB;

-- ============================================================
-- Departments table (replaces 'department')
-- ============================================================
CREATE TABLE departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    head_doctor VARCHAR(100),
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Rooms table (replaces 'Room')
-- ============================================================
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('GENERAL', 'PRIVATE', 'ICU') NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    INDEX idx_availability (is_available, room_type)
) ENGINE=InnoDB;

-- ============================================================
-- Patients table (replaces 'patient_info')
-- ============================================================
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    id_type ENUM('AADHAR', 'VOTER_ID', 'DRIVING_LICENSE', 'PASSPORT') NOT NULL,
    id_number VARCHAR(20) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    age INT NOT NULL,
    disease VARCHAR(200) NOT NULL,
    room_id INT,
    admission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deposit DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('ADMITTED', 'DISCHARGED') DEFAULT 'ADMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_id (id_type, id_number),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_name (full_name)
) ENGINE=InnoDB;

-- ============================================================
-- Employees table (replaces 'EMP_INFO')
-- ============================================================
CREATE TABLE employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    department_id INT,
    phone VARCHAR(15) NOT NULL,
    salary DECIMAL(12,2) NOT NULL,
    email VARCHAR(100) UNIQUE,
    aadhar_number VARCHAR(12),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(dept_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Doctors table (NEW feature)
-- ============================================================
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE, 
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    department_id INT,
    phone VARCHAR(15),
    email VARCHAR(100),
    consultation_fee DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(dept_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Appointments table (NEW feature)
-- ============================================================
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    patient_name VARCHAR(100) NOT NULL, 
    patient_phone VARCHAR(15) NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE SET NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Ambulances table (replaces 'Ambulance')
-- ============================================================
CREATE TABLE ambulances (
    ambulance_id INT AUTO_INCREMENT PRIMARY KEY,
    driver_name VARCHAR(100) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    vehicle_name VARCHAR(100) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    location VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Discharge Records (replaces 'patient_discharge')
-- ============================================================
CREATE TABLE discharge_records (
    discharge_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    room_id INT,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deposit DECIMAL(10,2),
    total_bill DECIMAL(12,2),
    discharged_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL,
    FOREIGN KEY (discharged_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Bills table (NEW feature)
-- ============================================================
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    room_charges DECIMAL(10,2) DEFAULT 0,
    doctor_fees DECIMAL(10,2) DEFAULT 0,
    other_charges DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    deposit_paid DECIMAL(10,2) DEFAULT 0,
    balance_due DECIMAL(12,2) NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'PAID', 'PARTIAL') DEFAULT 'PENDING',
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- Notifications table (NEW feature)
-- ============================================================
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    type ENUM('INFO', 'WARNING', 'CRITICAL', 'SUCCESS') DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Audit Logs table (NEW feature)
-- ============================================================
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Inventory table (NEW feature)
-- ============================================================
CREATE TABLE inventory (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL UNIQUE,
    category ENUM('MEDICINE', 'CONSUMABLE', 'EQUIPMENT') DEFAULT 'MEDICINE',
    quantity INT DEFAULT 0,
    unit_price DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Medical History table (NEW feature)
-- ============================================================
CREATE TABLE medical_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    allergies VARCHAR(255),
    notes TEXT,
    recorded_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Prescriptions table (NEW feature)
-- ============================================================
CREATE TABLE prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    medicine_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    notes TEXT,
    prescribed_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Lab Tests table (NEW feature)
-- ============================================================
CREATE TABLE lab_tests (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT,
    test_name VARCHAR(100) NOT NULL,
    test_result TEXT,
    test_date DATE NOT NULL,
    status ENUM('PENDING', 'COMPLETED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Seed Data
-- ============================================================

-- Default admin user (password: admin123, will be hashed on first login)
INSERT INTO users (username, password_hash, full_name, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('reception', 'admin123', 'Reception Staff', 'RECEPTIONIST');


-- Sample departments
INSERT INTO departments (dept_name, head_doctor, phone) VALUES
('Cardiology', 'Dr. Sharma', '9876543210'),
('Orthopedics', 'Dr. Patel', '9876543211'),
('Neurology', 'Dr. Gupta', '9876543212'),
('Pediatrics', 'Dr. Singh', '9876543213'),
('General Medicine', 'Dr. Desai', '9876543214'),
('Emergency', 'Dr. Khan', '9876543215');

-- Sample doctors
INSERT INTO doctors (full_name, specialization, department_id, phone, consultation_fee) VALUES
('Dr. Sharma', 'Cardiologist', 1, '9876543210', 1000.00),
('Dr. Patel', 'Orthopedic Surgeon', 2, '9876543211', 800.00),
('Dr. Gupta', 'Neurologist', 3, '9876543212', 1200.00);

-- Sample rooms
INSERT INTO rooms (room_number, room_type, price_per_day, is_available) VALUES
('G-101', 'GENERAL', 500.00, FALSE),
('G-102', 'GENERAL', 500.00, FALSE),
('G-103', 'GENERAL', 500.00, FALSE),
('G-104', 'GENERAL', 500.00, TRUE),
('G-105', 'GENERAL', 500.00, TRUE),
('P-201', 'PRIVATE', 2000.00, TRUE),
('P-202', 'PRIVATE', 2000.00, TRUE),
('P-203', 'PRIVATE', 2000.00, TRUE),
('P-204', 'PRIVATE', 2000.00, TRUE),
('P-205', 'PRIVATE', 2000.00, TRUE),
('ICU-301', 'ICU', 5000.00, TRUE),
('ICU-302', 'ICU', 5000.00, TRUE),
('ICU-303', 'ICU', 5000.00, TRUE),
('ICU-304', 'ICU', 5000.00, TRUE),
('ICU-305', 'ICU', 5000.00, TRUE);

-- Sample inventory
INSERT INTO inventory (item_name, category, quantity, unit_price) VALUES
('Paracetamol 500mg', 'MEDICINE', 1000, 2.00),
('Amoxicillin 250mg', 'MEDICINE', 500, 5.00),
('Syringe 5ml', 'CONSUMABLE', 2000, 10.00),
('Bandages', 'CONSUMABLE', 5000, 5.00);

-- Sample patients
INSERT INTO patients (id_type, id_number, contact, full_name, gender, age, disease, room_id, deposit, status) VALUES
('AADHAR', '123456789012', '9876543210', 'Rahul Patil', 'MALE', 45, 'Viral Fever', 1, 5000.00, 'ADMITTED'),
('AADHAR', '123456789013', '9876543211', 'Priya Sharma', 'FEMALE', 32, 'Fracture', NULL, 0.00, 'DISCHARGED'),
('PASSPORT', 'A1234567', '9876543212', 'Anil Kumar', 'MALE', 55, 'Cardiac Arrest', 2, 10000.00, 'ADMITTED'),
('VOTER_ID', 'VOT123456', '9876543213', 'Neha Singh', 'FEMALE', 28, 'Dengue', NULL, 0.00, 'DISCHARGED'),
('AADHAR', '123456789014', '9876543214', 'Suresh Verma', 'MALE', 60, 'Covid-19', 3, 2000.00, 'ADMITTED');
