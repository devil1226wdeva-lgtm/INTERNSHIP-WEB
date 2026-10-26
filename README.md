# ðŸŽ“ Student Internship Tracker (Project ID: 122601)

A full-stack Student Internship Management System built with a **Java Spring Boot** backend, **Spring Data JPA**, **Spring Security**, **MySQL**, and an integrated responsive **HTML5/CSS3/Vanilla JavaScript** frontend.

---

## ðŸ“‹ Table of Contents
1. [Architecture & Tech Stack](#architecture--tech-stack)
2. [Prerequisites & Software Requirements](#prerequisites--software-requirements)
3. [MySQL Database Setup](#mysql-database-setup)
4. [Environment Configuration](#environment-configuration)
5. [Running the Application](#running-the-application)
6. [Accessing the Frontend](#accessing-the-frontend)
7. [REST API Documentation](#rest-api-documentation)
8. [Automated Testing & Results](#automated-testing--results)
9. [Common Troubleshooting & Solutions](#common-troubleshooting--solutions)

---

## 1. Architecture & Tech Stack

- **Backend**: Java 21 LTS with Spring Boot 3.2.5
- **ORM & Data Access**: Spring Data JPA, Hibernate 6
- **Database**: MySQL 8.0 (with H2 in-memory mode for rapid isolated automated tests)
- **Security**: Spring Security with BCrypt password hashing & secure HTTP session management
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Size`)
- **Frontend**: Clean semantic HTML5, Vanilla CSS3 (responsive design), and JavaScript `fetch()` API client
- **Build Tool**: Apache Maven 3.9.6 (with included `./mvnw.cmd` wrapper)

### Project Structure
```text
C:\project 122601\
â”œâ”€â”€ .env.example                                  # Sample environment variables
â”œâ”€â”€ .tools\apache-maven-3.9.6\                   # Local Maven toolchain
â”œâ”€â”€ mvnw.cmd                                     # Automated build & run wrapper
â”œâ”€â”€ pom.xml                                      # Maven dependencies & build configuration
â”œâ”€â”€ README.md                                    # Comprehensive system documentation
â”œâ”€â”€ src\
â”‚   â”œâ”€â”€ main\
â”‚   â”‚   â”œâ”€â”€ java\com\student\internshiptracker\
â”‚   â”‚   â”‚   â”œâ”€â”€ InternshipTrackerApplication.java # Spring Boot entry point
â”‚   â”‚   â”‚   â”œâ”€â”€ config\                           # SecurityConfig, WebConfig, CORS
â”‚   â”‚   â”‚   â”œâ”€â”€ controller\                       # Auth, Internship, Dashboard, Profile, Settings
â”‚   â”‚   â”‚   â”œâ”€â”€ dto\                              # Request & Response Transfer Objects
â”‚   â”‚   â”‚   â”œâ”€â”€ entity\                           # Student, Internship, ApplicationStatus
â”‚   â”‚   â”‚   â”œâ”€â”€ exception\                        # GlobalExceptionHandler, Custom Exceptions
â”‚   â”‚   â”‚   â”œâ”€â”€ repository\                       # StudentRepository, InternshipRepository
â”‚   â”‚   â”‚   â”œâ”€â”€ security\                         # CustomUserDetails, CustomUserDetailsService
â”‚   â”‚   â”‚   â””â”€â”€ service\                          # Service interfaces & implementations
â”‚   â”‚   â””â”€â”€ resources\
â”‚   â”‚       â”œâ”€â”€ application.properties           # Spring Boot & MySQL configuration
â”‚   â”‚       â”œâ”€â”€ schema.sql                       # Database creation DDL script
â”‚   â”‚       â””â”€â”€ static\                          # Embedded frontend (HTML, CSS, JS)
â”‚   â”‚           â”œâ”€â”€ login.html
â”‚   â”‚           â”œâ”€â”€ register.html
â”‚   â”‚           â”œâ”€â”€ dashboard.html
â”‚   â”‚           â”œâ”€â”€ internships.html
â”‚   â”‚           â”œâ”€â”€ add-internship.html
â”‚   â”‚           â”œâ”€â”€ profile.html
â”‚   â”‚           â”œâ”€â”€ settings.html
â”‚   â”‚           â””â”€â”€ js\app.js                     # Unified frontend API & UI logic
â”‚   â””â”€â”€ test\
â”‚       â”œâ”€â”€ java\com\student\internshiptracker\  # 16 Automated Integration & Unit Tests
â”‚       â””â”€â”€ resources\application.properties     # H2 MySQL-compatibility test configuration
â””â”€â”€ internship management projct(122601)\         # Original frontend folder (preserved)
```

---

## 2. Prerequisites & Software Requirements

- **Java JDK**: Java 17 or Java 21 LTS
- **Build Tool**: Maven 3.8+ (an automated wrapper script `mvnw.cmd` is pre-configured in this repository)
- **Database**: MySQL Server 8.0+
- **Browser**: Any modern web browser (Chrome, Edge, Firefox)

---

## 3. MySQL Database Setup

### Step 1: Start MySQL Server
Ensure your local MySQL service is running:
```powershell
net start MySQL80
```
*(Or start it via MySQL Workbench / Windows Services).*

### Step 2: Create the Database Schema
You can either let Spring Boot create the database automatically on startup (`createDatabaseIfNotExist=true` in `application.properties`), or run the provided `src/main/resources/schema.sql` script in MySQL CLI or MySQL Workbench:

```sql
CREATE DATABASE IF NOT EXISTS internship_tracker
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE internship_tracker;

-- Students table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    student_id VARCHAR(50),
    department VARCHAR(100),
    academic_year VARCHAR(30),
    college VARCHAR(150),
    skills VARCHAR(500),
    theme VARCHAR(20) DEFAULT 'light',
    email_updates BOOLEAN DEFAULT FALSE,
    application_reminders BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_students_username (username),
    INDEX idx_students_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Internships table
CREATE TABLE IF NOT EXISTS internships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    company VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    applied_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    description TEXT,
    internship_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    website_url VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_internships_student (student_id),
    INDEX idx_internships_status (status),
    INDEX idx_internships_company (company),
    CONSTRAINT fk_internships_student
        FOREIGN KEY (student_id) REFERENCES students(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 4. Environment Configuration

The application uses standard Spring Boot externalized configuration. Copy `.env.example` to configure your credentials or set them in your terminal:

```powershell
# Set environment variables in PowerShell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/internship_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "your_mysql_password"
```

If not provided, the application defaults to:
- URL: `jdbc:mysql://localhost:3306/internship_tracker`
- User: `root`
- Password: (blank)

---

## 5. Running the Application

### Option A: Using the Maven Wrapper (Recommended)
From the project root directory, run:
```powershell
.\mvnw.cmd spring-boot:run
```

### Option B: Running the Packaged Standalone JAR
To run the pre-built, production-ready JAR:
```powershell
java -jar target\internship-tracker-1.0.0.jar
```

The server starts on port `8080`.

---

## 6. Accessing the Frontend

Once the server is running, open your browser and navigate to:
ðŸ‘‰ **`http://localhost:8080/login.html`** (or simply **`http://localhost:8080/`**)

### Application Pages & Features:
1. **Register** (`/register.html`): Create a new student account (Username, Email, Password, Name, Student ID).
2. **Login** (`/login.html`): Log in using your username or email and password.
3. **Dashboard** (`/dashboard.html`): Displays real-time statistics (Total Internships, Applications, Interviews, Offers/Selected) and the most recent 5 applications.
4. **Internships** (`/internships.html`): Full interactive list of applications with search, status filtering, and Edit/Delete controls.
5. **Add / Edit Internship** (`/add-internship.html`): Add a new internship or edit an existing one (`?id=X`).
6. **Student Profile** (`/profile.html`): View and update name, student ID, department, year, college, and skills.
7. **Settings** (`/settings.html`): Manage username, email address, password change, and notification preferences.
8. **Logout**: Safely destroys the server session and redirects to login.

---

## 7. REST API Documentation

All API responses follow a consistent JSON format:
```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... }
}
```

### Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new student | `{ "username": "student1", "email": "student1@example.com", "password": "password123", "name": "Student Name", "studentId": "122601" }` |
| `POST` | `/api/auth/login` | Log in | `{ "username": "student1", "password": "password123" }` |
| `POST` | `/api/auth/logout` | Terminate session | None |
| `GET` | `/api/auth/me` | Current authenticated student | None |

### Internship Endpoints (`/api/internships`)
*Protected: Student only accesses their own records.*
| Method | Endpoint | Description | Query / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/internships` | List all internships | `?search=google&status=Applied&location=Chennai` |
| `GET` | `/api/internships/{id}` | Get single internship | None |
| `POST` | `/api/internships` | Create new internship | `{ "company": "TCS", "role": "Java Intern", "location": "Chennai", "appliedDate": "2026-09-10", "status": "Applied", "description": "Notes" }` |
| `PUT` | `/api/internships/{id}` | Update internship details | Complete updated JSON payload |
| `PATCH`| `/api/internships/{id}/status`| Quick status update | `{ "status": "Interview" }` |
| `DELETE`| `/api/internships/{id}` | Delete internship | None |

### Dashboard Endpoints (`/api/dashboard`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard/stats` | Returns real-time counts (`total`, `applications`, `interviews`, `selected`, `rejected`, `completed`) |
| `GET` | `/api/dashboard/recent` | Returns the top 5 most recent internship applications |

### Profile & Settings Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/profile` | Retrieve student profile details |
| `PUT` | `/api/profile` | Update profile (name, studentId, department, year, email, college, skills) |
| `GET` | `/api/settings` | Retrieve account settings and preferences |
| `PUT` | `/api/settings/account` | Update account username and email |
| `PUT` | `/api/settings/password` | Change password with current password verification |
| `PUT` | `/api/settings/preferences` | Update theme (`light`/`dark`), emailUpdates, applicationReminders |

---

## 8. Automated Testing & Results

The test suite runs using Spring Boot Test and an in-memory MySQL-compatible database to verify all business rules, authentication, and data isolation constraints independently of the local MySQL daemon.

### Run Tests:
```powershell
.\mvnw.cmd test
```

### Test Coverage Summary:
- **`AuthControllerTest`** (5 tests):
  - Student registration
  - Duplicate username & duplicate email prevention
  - Validation failure handling
  - Login authentication & bad credentials rejection
  - Unauthorized access protection for `/api/auth/me`
- **`InternshipControllerTest`** (7 tests):
  - Adding new internship
  - Retrieving only owned internships
  - Data isolation: Student B is forbidden (`403`) from viewing Student A's records
  - Updating internship details
  - Updating status via PATCH
  - Deleting own internship
  - Data isolation: Student B is forbidden (`403`) from deleting Student A's records
- **`DashboardControllerTest`** (2 tests):
  - Accurate live statistics calculation from database
  - Recent internships ordering and retrieval
- **`ProfileAndSettingsTest`** (2 tests):
  - Profile retrieval and update
  - Account update, password verification & change, preferences update

**Test Results: 16 Tests Run, 0 Failures, 0 Errors, 0 Skipped (BUILD SUCCESS)**

---

## 9. Common Troubleshooting & Solutions

1. **`CommunicationsException: Communications link failure`**
   - **Cause**: MySQL server is not running on port 3306.
   - **Fix**: Start the MySQL service using `net start MySQL80` or via MySQL Workbench.

2. **`Access denied for user 'root'@'localhost'`**
   - **Cause**: Incorrect MySQL password.
   - **Fix**: Set `$env:SPRING_DATASOURCE_PASSWORD="your_password"` in PowerShell before starting the application.

3. **`401 Unauthorized` on API calls**
   - **Cause**: The user's session has expired or the user is not logged in.
   - **Fix**: Navigate to `login.html` and log in again.
