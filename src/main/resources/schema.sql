-- ===================================================================
-- Database Creation Script: Student Internship Tracker
-- ===================================================================

CREATE DATABASE IF NOT EXISTS internship_tracker
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE internship_tracker;

-- -------------------------------------------------------------------
-- Table structure for students
-- -------------------------------------------------------------------
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

-- -------------------------------------------------------------------
-- Table structure for internships
-- -------------------------------------------------------------------
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
