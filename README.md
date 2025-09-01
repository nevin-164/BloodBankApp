# PLASMIC - Blood Bank Management System
<!-- Replace with your actual logo URL if you have one -->

PLASMIC is a comprehensive, multi-user web application built with **Java Servlets and JSP**, designed to streamline the operations of a modern blood bank network.  

It provides distinct portals for **Admins, Hospitals, Donors, and Patients**, each with tailored functionalities to manage the entire lifecycle of blood donation and transfusion.  

---

## ✨ Key Features  

PLASMIC is built around four key user roles, each with a dedicated and secure dashboard.  

### 👤 Admin Portal
The central command center for the entire PLASMIC network.  
- **Central Dashboard:** An overview of all management options.  
- **User Management:** View, edit, and delete Donors and Patients in separate, organized lists.  
- **Hospital Management:** Add new hospitals, edit details, and remove them.  
- **Stock Management:** View and manually update blood stock levels for any registered hospital.  
- **Expiry Alerts:** Dedicated page to monitor blood units nearing expiry.  

### 🏥 Hospital Portal
Each hospital manages its own operations.  
- **Secure, Separate Login**  
- **Inventory Management:** View and update their own blood stock in real-time.  
- **Patient Request Management:**  
  - Approve → auto-deduct units from inventory and mark fulfilled.  
  - Decline → hides the request from their view but keeps it visible to others.  
- **Donation Appointment Management:**  
  - Approve → adds units to stock + updates donor eligibility.  
  - Decline → removes from schedule.  

### ❤️ Donor Portal
For donors to contribute easily.  
- **Secure Registration & Login**  
- **Appointment Booking:** Request donation at a hospital.  
- **Eligibility Tracking:** Prevents booking if within 90-day cooldown.  
- **Status Notifications:** Approved/declined status shown on dashboard.  

### 🩸 Patient Portal
Simple and efficient for patients.  
- **Secure Registration & Login**  
- **Blood Request System:** Broadcasts requests to all hospitals in the network.  

---

## 🛠️ Technology Stack
- **Backend:** Java Servlets  
- **Frontend:** JSP, JSTL, HTML, CSS, JavaScript  
- **Database:** MySQL  
- **Web Server:** Apache Tomcat  
- **Build Tool:** Maven (or manual library management)  

---

## 🚀 How to Run the Project

### Prerequisites
- JDK 11+  
- Apache Tomcat 9+  
- MySQL Server  

---

### 1. Database Setup
Create the database and tables in MySQL:

```sql
CREATE DATABASE bloodbank;

USE bloodbank;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL, -- ADMIN, DONOR, PATIENT
    contact_number VARCHAR(20),
    blood_group VARCHAR(5),
    last_donation_date DATE,
    next_eligible_date DATE
);

CREATE TABLE hospitals (
    hospital_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    address TEXT
);

CREATE TABLE donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    hospital_id INT,
    units INT NOT NULL,
    blood_group VARCHAR(5),
    donation_date DATE,
    appointment_date DATE,
    expiry_date DATE,
    status VARCHAR(15) DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE
);

CREATE TABLE requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    blood_group VARCHAR(5) NOT NULL,
    units_requested INT NOT NULL,
    status VARCHAR(15) DEFAULT 'PENDING',
    request_date DATE,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE blood_stock (
    hospital_id INT NOT NULL,
    blood_group VARCHAR(3) NOT NULL,
    units INT NOT NULL DEFAULT 0,
    PRIMARY KEY (hospital_id, blood_group),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE
);

CREATE TABLE request_actions (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT,
    hospital_id INT,
    action VARCHAR(15),
    FOREIGN KEY (request_id) REFERENCES requests(request_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE
);

-- Add first admin user
INSERT INTO users (name, email, password, role, contact_number)
VALUES ('Admin', 'admin@plasmic.com', 'admin123', 'ADMIN', '1234567890');
```

### 2. Configure Database Connection
Open the src/main/java/dao/DBUtil.java file and update the database URL, username, and password to match your local MySQL setup.
```
// Inside DBUtil.java
private static final String URL = "jdbc:mysql://localhost:3306/bloodbank";
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```
### 3. Build and Deploy
Clean and Build: Clean and build your project using your IDE (e.g., in Eclipse, Project > Clean...).

Deploy to Tomcat: Deploy the project to your Apache Tomcat server.

Start the Server: Start the Tomcat server.

### 4. Access the Application
Open your web browser and navigate to: http://localhost:8080/YourAppName/ (the application name might be BloodBankApp or something similar).

You will be greeted by the PLASMIC welcome page.

### 🌟 Future Enhancements
- Password Hashing: Implement a library like jBcrypt to securely hash all user and hospital passwords.

- Admin Analytics: Create a visual dashboard for the admin with charts showing donation trends, stock levels over time, etc.

- Email Notifications: Integrate a library like Jakarta Mail to send email confirmations to donors for their appointments.
---
