-- ============================================================
-- Student Internship Tracker — Supabase PostgreSQL Schema
-- Run this in: Supabase Dashboard → SQL Editor → New Query
-- ============================================================

-- Drop tables if re-running (safe reset)
DROP TABLE IF EXISTS internships CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TYPE IF EXISTS application_status CASCADE;

-- -------------------------------------------------------
-- ENUM: Application Status
-- -------------------------------------------------------
CREATE TYPE application_status AS ENUM (
    'INTERESTED',
    'APPLIED',
    'ASSESSMENT',
    'INTERVIEW',
    'SELECTED',
    'REJECTED',
    'COMPLETED'
);

-- -------------------------------------------------------
-- TABLE: students
-- -------------------------------------------------------
CREATE TABLE students (
    id                    BIGSERIAL    PRIMARY KEY,
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

-- -------------------------------------------------------
-- TABLE: internships
-- -------------------------------------------------------
CREATE TABLE internships (
    id               BIGSERIAL          PRIMARY KEY,
    company          VARCHAR(100)       NOT NULL,
    role             VARCHAR(100)       NOT NULL,
    location         VARCHAR(100),
    applied_date     DATE,
    status           VARCHAR(30)        NOT NULL DEFAULT 'APPLIED',
    description      VARCHAR(2000),
    internship_type  VARCHAR(50),
    start_date       DATE,
    end_date         DATE,
    website_url      VARCHAR(255),
    notes            VARCHAR(1000),
    student_id       BIGINT             NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    created_at       TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP          NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_internship_student ON internships(student_id);
CREATE INDEX idx_internship_status  ON internships(status);
CREATE INDEX idx_internship_company ON internships(company);

-- -------------------------------------------------------
-- AUTO-UPDATE updated_at trigger
-- -------------------------------------------------------
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

-- -------------------------------------------------------
-- Verify
-- -------------------------------------------------------
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
