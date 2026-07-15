<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Swing-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Swing" />
  <img src="https://img.shields.io/badge/FlatLaf-4CAF50?style=for-the-badge&logo=java&logoColor=white" alt="FlatLaf" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
</div>

<h1 align="center">Last Moment Hospital (LMH) 🏥</h1>

<p align="center">
  <strong>An enterprise-grade Hospital Management System designed to streamline healthcare administration.</strong>
</p>

## 📖 Introduction
**Last Moment Hospital (LMH)** is a complete Hospital Management System built with Java 17 and MySQL. It aims to digitize and automate the day-to-day administrative operations of a healthcare facility. Built to reduce manual errors, speed up data retrieval, and optimize resource allocation, LMH provides an end-to-end solution—from patient registration and room management to discharge and billing.

Recently refactored into a modern **3-Tier MVC Architecture**, LMH utilizes enterprise patterns such as Connection Pooling (HikariCP), Password Hashing (BCrypt), and Data Access Objects (DAO).

## ✨ Features
- 📊 **Interactive Dashboard:** Beautiful charts (JFreeChart) and real-time statistics covering revenue, occupancy, and daily appointments.
- 🧑‍⚕️ **Patient & Doctor Management:** Comprehensive lifecycle management from admission to discharge, along with specialized doctor directories.
- 👥 **Employee & Department Management:** Centralized tracking of hospital staff and structural mapping of hospital units.
- 🛏️ **Room & Ward Management:** Real-time tracking of room availability and occupancy rates.
- 💵 **Automated Billing System:** Transparent invoicing encompassing room charges, doctor fees, and deposits.
- 📅 **Appointments & Prescriptions:** Manage outpatient appointments and digital prescriptions.
- 🔒 **Security & Audit Logging:** Role-Based Access Control (RBAC), BCrypt password hashing, and full audit trails tracking user actions.
- 🚑 **Ambulance Management:** Fleet tracking for emergency response.

## 🏗️ System Architecture
The application follows a clean **3-Tier Architecture**, ensuring clear separation of concerns, easier maintenance, and scalability.

1. **UI Layer (Presentation):** Built using Java Swing with the **FlatLaf** Look and Feel for a sleek, modern UI.
2. **Business Logic Layer (Service):** Manages core operations, data processing, input validation, and enforces business rules.
3. **Database Layer (Data Access):** Utilizes the DAO (Data Access Object) design pattern via JDBC and **HikariCP** for connection pooling.

## 💻 Tech Stack
| Technology | Purpose |
| :--- | :--- |
| **Java 17** | Core programming language |
| **Java Swing + FlatLaf** | Desktop UI Framework & Modern Theming |
| **MySQL** | Primary Relational Database |
| **Maven** | Build, dependency, and lifecycle management |
| **HikariCP** | High-performance JDBC connection pooling |
| **BCrypt (jBCrypt)** | Secure password hashing |
| **JFreeChart** | Interactive dashboard analytics |
| **SLF4J / Logback** | Application logging |

## 🚀 Installation Guide

### Prerequisites
- Java Development Kit (JDK 17 or higher recommended)
- MySQL Server
- Maven

### Step-by-Step Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Vaibhavpatil103/hospital-management-system.git
   cd hospital-management-system
   ```

2. **Configure MySQL Database:**
   - Open your MySQL CLI or Workbench.
   - Create the database and import the schema:
     ```bash
     mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS hospital_management_system;"
     mysql -u root -p hospital_management_system < src/main/resources/schema.sql
     ```

3. **Update Database Credentials:**
   - Navigate to `src/main/resources/db.properties`.
   - Update `db.username` and `db.password` to match your local MySQL configuration. 

4. **Seed the Database (Optional but Recommended):**
   - The project includes a `DataSeeder` utility that generates 50 realistic patients, appointments, and bills so the dashboard looks great immediately.
   - Run the seeder via Maven:
     ```bash
     mvn exec:java -Dexec.mainClass="hospital.management.system.util.DataSeeder"
     ```

5. **Build & Run the Application:**
   - Compile the project:
     ```bash
     mvn clean compile
     ```
   - Launch the application:
     ```bash
     mvn exec:java -Dexec.mainClass="hospital.management.system.HospitalManagementApp"
     ```

### Default Credentials
- **Admin Role:** Username: `admin` | Password: `admin123`
- **Receptionist Role:** Username: `reception` | Password: `admin123`

*(Note: Passwords are automatically BCrypt-hashed upon the first login).*

## 📂 Project Structure
```text
src/
└── main/
    ├── java/hospital/management/system/
    │   ├── config/      # Database connection pooling (HikariCP)
    │   ├── dao/         # Data Access Object layer 
    │   ├── model/       # Domain entities (POJOs)
    │   ├── service/     # Business logic & authentication
    │   ├── util/        # Helpers, Session Management, DataSeeder
    │   └── view/        # Swing GUI components, Dashboards, and Forms
    └── resources/       # Icons, db.properties, and schema.sql
```

## 🛡️ Security Features
- **Prepared Statements:** 100% prevention against SQL Injection attacks.
- **Password Hashing:** Passwords are never stored in plain text (jBCrypt).
- **Audit Logging:** Critical actions (Logins, Discharges, Updates) are tracked in the `audit_logs` table.
- **Connection Pooling:** HikariCP prevents database connection leaks and memory exhaustion.

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! 
1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request.

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

## ✍️ Author
**Vaibhav Patil**
- 💼 LinkedIn: [Vaibhav Patil](https://www.linkedin.com/in/vaibhav-patil02/)
- 🐙 GitHub: [@Vaibhavpatil103](https://github.com/Vaibhavpatil103)
- ✉️ Email: patilvaibhav0222@gmail.com

---
<div align="center">
  <i>If you found this project helpful, please consider giving it a ⭐ on GitHub!</i>
</div>
