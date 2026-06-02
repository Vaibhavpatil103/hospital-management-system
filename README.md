<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Swing-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Swing" />
  <img src="https://img.shields.io/badge/FlatLaf-4CAF50?style=for-the-badge&logo=java&logoColor=white" alt="FlatLaf" />
</div>

<h1 align="center">Last Moment Hospital (LMH) 🏥</h1>

<p align="center">
  <strong>A comprehensive and robust Hospital Management System designed to streamline healthcare administration.</strong>
</p>

## 📖 Introduction
**Last Moment Hospital (LMH)** is a complete Hospital Management System built with Java and MySQL. It aims to digitize and automate the day-to-day administrative operations of a healthcare facility. Built to reduce manual errors, speed up data retrieval, and optimize resource allocation, LMH provides an end-to-end solution—from patient registration and room management to discharge and billing.

Whether for a small clinic or a mid-sized hospital, LMH proves its capability as a robust enterprise application suitable for high-traffic environments, serving as a reliable backbone for healthcare administrators.

## ✨ Features
- 🧑‍⚕️ **Patient Management:** Comprehensive patient lifecycle management from admission to discharge.
- 👥 **Employee Management:** Centralized tracking of doctors, nurses, and administrative staff details.
- 🛏️ **Room Management:** Real-time tracking of room availability, occupancy rates, and ward management.
- 🏥 **Department Management:** Organized structure mapping doctors and staff to specific medical departments.
- 🚑 **Ambulance Management:** Fleet tracking for emergency response and dispatch operations.
- 💵 **Billing System:** Automated, transparent, and accurate invoicing integrated with patient services.
- 🚪 **Discharge Management:** Streamlined workflow for clearing patients and generating final reports.
- 🔐 **User Authentication:** Secure Role-Based Access Control (RBAC) ensuring data privacy.
- 📊 **Dashboard Analytics:** High-level overview of hospital metrics and operational status at a glance.
- 🗄️ **Database Integration:** Persistent, scalable, and secure data management using MySQL.

## 📸 Screenshots

| Login Screen | Dashboard |
| :---: | :---: |
| *(Add Screenshot Here)* | *(Add Screenshot Here)* |

| Patient Module | Employee Module |
| :---: | :---: |
| *(Add Screenshot Here)* | *(Add Screenshot Here)* |

| Billing Module | Room Management |
| :---: | :---: |
| *(Add Screenshot Here)* | *(Add Screenshot Here)* |

> *Replace the placeholders with actual screenshots from your running application.*

## 🏗️ System Architecture
The application follows a clean **3-Tier Architecture**, ensuring clear separation of concerns, easier maintenance, and scalability.

1. **UI Layer (Presentation):** Built using Java Swing with the FlatLaf Look and Feel. It handles all user interactions and ensures a modern, intuitive graphical interface.
2. **Business Logic Layer (Service):** Manages the core operations, data processing, input validation, and business rules, acting as the bridge between the UI and the data layer.
3. **Database Layer (Data Access):** Utilizes the DAO (Data Access Object) design pattern via JDBC to securely and efficiently communicate with the MySQL database.

## 🗃️ Database Design
The LMH system utilizes a highly normalized relational database to maintain data integrity.

### Main Tables & Data Flow
- **`users`**: Stores authentication credentials and role authorizations.
- **`patients`**: Central entity tracking demographics, assigned doctors, and current admission status.
- **`employees`**: Maintains staff details, linked to respective departments.
- **`departments`**: Categorizes hospital units (e.g., Cardiology, Neurology).
- **`rooms`**: Tracks physical bed availability, linked dynamically to patient admissions.
- **`billing` & `discharge`**: Connects patient records with financial data and discharge histories.

*Data flows seamlessly from the user input through the UI to the Business Layer, where it is sanitized and processed before being committed to the normalized MySQL tables.*

## 💻 Tech Stack
| Technology | Purpose |
| :--- | :--- |
| **Java (JDK 8+)** | Core programming language |
| **Java Swing** | Desktop UI Framework |
| **FlatLaf** | Modern UI Look and Feel for Swing |
| **JDBC** | Database connectivity and execution |
| **MySQL** | Primary Relational Database Management System |
| **Maven / Ant** | Build and dependency management |
| **IntelliJ IDEA / NetBeans** | Integrated Development Environment (IDE) |

## 🚀 Installation Guide

### Prerequisites
- Java Development Kit (JDK 11 or higher recommended)
- MySQL Server & MySQL Workbench
- Maven (if building from source)
- Git

### Step-by-Step Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Vaibhavpatil103/hospital-management-system.git
   cd hospital-management-system
   ```

2. **Configure MySQL Database:**
   - Open MySQL Workbench.
   - Run the provided SQL script to create the schema and tables:
     ```sql
     source src/main/resources/schema.sql;
     ```

3. **Update Database Credentials:**
   - Navigate to `src/main/resources/db.properties`.
   - Update the `db.username` and `db.password` to match your local MySQL configuration.

4. **Install Dependencies & Build:**
   ```bash
   mvn clean install
   ```

5. **Run the Application:**
   - Execute the main class `HospitalManagementApp.java` via your IDE, or run the compiled jar file.

## 📂 Project Structure
```text
hospital-management-system/
├── src/
│   └── main/
│       ├── java/hospital/management/system/
│       │   ├── config/      # Database connection and configuration utilities
│       │   ├── dao/         # Data Access Object layer for database operations
│       │   ├── model/       # Plain Old Java Objects (POJOs) representing DB entities
│       │   ├── service/     # Business logic and validation rules
│       │   ├── util/        # Helpers, Session Management, UI Factories
│       │   └── view/        # Swing GUI components and screens
│       └── resources/       # Icons, properties files, and SQL scripts
├── target/                  # Compiled output and generated artifacts
├── pom.xml                  # Maven configuration file
└── README.md                # Project documentation
```

## 🧩 Key Modules
- **Patients:** End-to-end flow handling new admissions, assigning rooms, updating medical records, and tracking current patient status.
- **Employees:** Complete directory for doctors, nurses, and administrative staff including contact information and departmental assignment.
- **Departments:** Structural management of hospital wings, enabling the categorization of specialized doctors.
- **Rooms:** Real-time inventory of hospital beds, automatically updating availability when patients are admitted or discharged.
- **Ambulance:** Logistics tracking for emergency vehicle dispatch, availability, and driver details.
- **Billing:** Automated cost calculation encompassing room charges, medication, and doctor fees, generating printable invoices.
- **Authentication:** Secure login gateway controlling system access based on user credentials.

## 🛡️ Security Features
- **Prepared Statements:** 100% prevention against SQL Injection attacks across all database queries.
- **Input Validation:** Strict UI-level constraints preventing invalid data, overflows, and formatting errors.
- **Authentication Controls:** Session-based validation ensuring only authorized personnel access sensitive patient records.
- **Database Security:** Principle of least privilege applied to database connections with modularized configuration.

## 🔮 Future Enhancements
- 👨‍⚕️ **Doctor Management & Appointment Scheduling:** Online booking system for outpatients.
- 💊 **Pharmacy Module:** Inventory management for medicines and automated prescription pricing.
- 🔬 **Laboratory Module:** Integration for requesting and viewing lab test results.
- 📈 **Report Generation:** PDF and Excel export features for financial and administrative audits.
- ☁️ **Cloud Deployment:** Migrating the database to AWS RDS for remote access.
- 📱 **REST APIs & Mobile App Integration:** Extending the system for mobile patient portals.

## ⚡ Performance & Scalability
LMH is designed with horizontal scalability in mind. The DAO pattern ensures that swapping the underlying database (e.g., from MySQL to PostgreSQL) requires minimal refactoring. Connection pooling can easily be integrated to handle thousands of concurrent transactions in a large-scale hospital environment, while the optimized Swing UI runs smoothly on low-end administrative hardware.

## 🎓 Learning Outcomes
This project serves as a comprehensive masterclass in core software engineering principles:
- **OOP Concepts:** Deep application of Inheritance, Encapsulation, Polymorphism, and Abstraction.
- **JDBC & Database Design:** Mastery of relational database schemas, normalization, and complex querying.
- **Swing UI Development:** Building responsive, modern desktop applications outside the standard web ecosystem.
- **Software Architecture:** Practical implementation of the MVC/3-Tier architecture and DAO design patterns.

## 💼 Resume Impact
**Why this project stands out:**
- **Full-Stack Development Skills:** Demonstrates the ability to build a complete application from the database schema up to the graphical user interface.
- **Database Skills:** Proves competence in modeling complex real-world relationships and ensuring data integrity.
- **Problem Solving:** Translates intricate real-world business requirements (hospital administration) into functional code.
- **Software Engineering Practices:** Showcases understanding of clean code, architectural patterns, and professional documentation.

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
