-- Migration script to move data from the legacy tables to the new normalized tables
-- Assumes the new schema (schema.sql) has already been created but is empty.
-- Also assumes the old tables (login, patient_info, room, department, emp_info, ambulance) still exist.

-- 1. Migrate Users (from login)
INSERT IGNORE INTO users (username, password_hash, full_name, role, is_active)
SELECT 
    ID, 
    PW, -- Note: Legacy passwords were plaintext. You must run a separate Java utility to hash them with BCrypt, or set a default hashed password and require users to reset.
    'Migrated User',
    'RECEPTIONIST', -- Default role for legacy users
    TRUE
FROM login;

-- 2. Migrate Departments
INSERT IGNORE INTO departments (dept_name, phone)
SELECT 
    Department, 
    Phone_no 
FROM department;

-- 3. Migrate Employees (from emp_info)
INSERT IGNORE INTO employees (full_name, age, phone, salary, email, aadhar_number)
SELECT 
    Name, 
    Age, 
    Phone_Number, 
    salary, 
    Email, 
    Aadhar_Number
FROM emp_info;
-- Note: Department IDs would need manual mapping since the old system didn't use foreign keys.

-- 4. Migrate Rooms (from room)
INSERT IGNORE INTO rooms (room_number, room_type, price_per_day, is_available)
SELECT 
    room_no,
    CASE 
        WHEN room_Type = 'General' THEN 'GENERAL'
        WHEN room_Type = 'Private' THEN 'PRIVATE'
        WHEN room_Type = 'ICU' THEN 'ICU'
        ELSE 'GENERAL'
    END,
    CAST(Price AS DECIMAL(10,2)),
    CASE 
        WHEN Availability = 'Available' THEN TRUE 
        ELSE FALSE 
    END
FROM room;

-- 5. Migrate Patients (from patient_info)
INSERT IGNORE INTO patients (id_type, id_number, contact, full_name, gender, disease, room_id, deposit, status, admission_time)
SELECT 
    ID,
    Contact,
    Contact,
    Name,
    CASE 
        WHEN Gender = 'Male' THEN 'MALE'
        WHEN Gender = 'Female' THEN 'FEMALE'
        ELSE 'OTHER'
    END,
    Patient_Disease,
    (SELECT room_id FROM rooms WHERE room_number = p.Room_Number LIMIT 1),
    CAST(Deposite AS DECIMAL(10,2)),
    'ADMITTED',
    STR_TO_DATE(Time, '%d/%m/%Y %H:%i:%s') -- Common format in legacy HMS: DD/MM/YYYY HH:mm:ss, but fallback is used later if it fails
FROM patient_info p;

-- 6. Migrate Ambulances (from ambulance)
INSERT IGNORE INTO ambulances (driver_name, vehicle_name, contact, is_available, location)
SELECT 
    Name,
    Car_name,
    Contact,
    CASE 
        WHEN Available = 'Available' THEN TRUE 
        ELSE FALSE 
    END,
    Location
FROM ambulance;

-- After migration is verified, you can drop the legacy tables:
-- DROP TABLE IF EXISTS login, patient_info, room, department, emp_info, ambulance;
