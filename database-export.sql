-- ===================================================================
-- Student Internship Tracker — Complete Database Export & Dump
-- Engine: PostgreSQL 17 (Supabase Cloud)
-- Exported At: 2026-10-27
-- ===================================================================

-- 1. CLEANUP & EXTENSIONS
DROP TABLE IF EXISTS internships CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- 2. STUDENTS TABLE SCHEMA
CREATE TABLE students (
    id                    BIGSERIAL PRIMARY KEY,
    username              VARCHAR(50)  NOT NULL UNIQUE,
    email                 VARCHAR(100) NOT NULL UNIQUE,
    password              VARCHAR(255) NOT NULL,
    full_name             VARCHAR(100),
    student_id            VARCHAR(50),
    department            VARCHAR(100),
    academic_year         VARCHAR(30),
    college               VARCHAR(150),
    skills                VARCHAR(500),
    theme                 VARCHAR(20)  NOT NULL DEFAULT 'light',
    email_updates         BOOLEAN      NOT NULL DEFAULT FALSE,
    application_reminders BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_students_username ON students(username);
CREATE INDEX idx_students_email    ON students(email);

-- 3. INTERNSHIPS TABLE SCHEMA
CREATE TABLE internships (
    id               BIGSERIAL    PRIMARY KEY,
    company          VARCHAR(100) NOT NULL,
    role             VARCHAR(100) NOT NULL,
    location         VARCHAR(100),
    applied_date     DATE,
    status           VARCHAR(30)  NOT NULL DEFAULT 'APPLIED',
    description      VARCHAR(2000),
    internship_type  VARCHAR(50),
    start_date       DATE,
    end_date         DATE,
    website_url      VARCHAR(255),
    notes            VARCHAR(1000),
    student_id       BIGINT       NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_internship_student ON internships(student_id);
CREATE INDEX idx_internship_status  ON internships(status);
CREATE INDEX idx_internship_company ON internships(company);

-- 4. AUTO-UPDATE TRIGGER FUNCTION
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_internships_updated_at
    BEFORE UPDATE ON internships
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 5. CURRENT DATA DUMP
INSERT INTO students (id, username, email, password, full_name, student_id, department, academic_year, college, skills, theme, email_updates, application_reminders, created_at, updated_at)
VALUES 
(1, 'testdeva', 'deva@example.com', '$2a$10$w8TKnPZ9D8iP3qDqW/7h2.9Lz9wW6Uj8L9oM2tV4x6h0lX8pG4', 'testdeva', NULL, NULL, NULL, NULL, NULL, 'light', false, false, '2026-10-27 19:02:24', '2026-10-27 19:02:24'),
(2, 'deva', 'deva@gmail.com', '$2a$10$w8TKnPZ9D8iP3qDqW/7h2.9Lz9wW6Uj8L9oM2tV4x6h0lX8pG4', 'Devanath', '122601', 'Computer Science & Engineering', '3rd Year', 'College of Engineering & Technology', 'Java, Spring Boot, React, MySQL, Supabase', 'light', true, true, '2026-10-27 19:09:07', '2026-10-27 19:09:07');

INSERT INTO internships (id, company, role, location, applied_date, status, description, internship_type, start_date, end_date, website_url, notes, student_id, created_at, updated_at)
VALUES 
(1, 'Google', 'Software Engineering Intern', 'Bangalore, India', '2026-10-27', 'APPLIED', 'Working on distributed systems and cloud backends', 'Full-time', NULL, NULL, 'https://careers.google.com', NULL, 1, '2026-10-27 19:03:45', '2026-10-27 19:03:45');

-- Reset sequences to prevent duplicate key errors on future inserts
SELECT setval('students_id_seq', (SELECT MAX(id) FROM students));
SELECT setval('internships_id_seq', (SELECT MAX(id) FROM internships));
