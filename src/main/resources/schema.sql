-- ============================================================
-- Hospital Management System v2.0 — Database Schema
-- LAST MOMENT HOSPITAL
-- ============================================================

-- Drop tables in reverse dependency order (if recreating)
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS discharge_records;
DROP TABLE IF EXISTS patients;
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

-- Sample rooms
INSERT INTO rooms (room_number, room_type, price_per_day, is_available) VALUES
('G-101', 'GENERAL', 500.00, TRUE),
('G-102', 'GENERAL', 500.00, TRUE),
('G-103', 'GENERAL', 500.00, TRUE),
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
