# Student Internship Tracker — Master Project Prompt & Specification

> **How to use**: Copy the complete prompt block below and paste it into any AI coding assistant (Antigravity, Claude Code, Cursor, ChatGPT, etc.) to recreate, extend, or explain this full-stack application from scratch.

---

```markdown
Act as a Principal Full-Stack Software Engineer and Senior UI/UX Designer specializing in Java Spring Boot, Cloud PostgreSQL (Supabase), and Apple-inspired Liquid Glass design systems.

Your objective is to design, develop, and deploy a complete, production-grade web application named "Student Internship Tracker".

=====================================================================
1. PROJECT OVERVIEW & ARCHITECTURAL PATTERN
=====================================================================
Build an end-to-end full-stack Student Internship Tracker that allows college and university students to track, manage, analyze, and optimize their internship applications, interview cycles, and placement offers.

Key Architectural Requirements:
1. Dual-Mode Architecture:
   - Server-Backed Mode: Java 21 + Spring Boot 3.2.5 REST API connected to Supabase Cloud PostgreSQL 17 via HikariCP connection pooling.
   - Static/Edge Fallback Mode: When hosted on static hosts (Vercel / Netlify) where no Java runtime exists, the frontend seamlessly intercepts 404 responses and persists all CRUD, authentication, profile, settings, and dashboard operations to a client-side localStorage engine (`ClientStore`) with zero user-facing errors.
2. Zero Frontend Build Tooling Requirement:
   - Written in vanilla HTML5, semantic CSS3 (no Tailwind or Bootstrap dependency), and modular Vanilla JavaScript (`fetch` API).
3. Cloud Native & Production Ready:
   - Database: Supabase PostgreSQL 17 (AWS `ap-northeast-1` Tokyo).
   - Deployment Targets: Vercel (clean URLs), Netlify, or self-hosted Java JAR (`java -jar target/internship-tracker-1.0.0.jar`).

=====================================================================
2. TECHNOLOGY STACK SPECIFICATION
=====================================================================
- Backend Language: Java 21 (LTS)
- Backend Framework: Spring Boot 3.2.5
- Build Tool: Apache Maven (`mvnw` wrapper)
- Security: Spring Security 6 with BCrypt password hashing (`BCryptPasswordEncoder`)
- Persistence/ORM: Spring Data JPA + Hibernate 6.4
- Database Driver: PostgreSQL JDBC Driver (`org.postgresql:postgresql:42.6.2`)
- Connection Pool: HikariCP 5.0 with connection validation and timeout tuning
- Cloud Database: Supabase Cloud PostgreSQL 17 via PgBouncer Session Pooler (port 5432)
- Frontend: Vanilla HTML5, CSS3 Glassmorphism, Modern Vanilla JavaScript (ES6+)
- Design Style: Apple Liquid Glass / Translucent Frosted Glassmorphism

=====================================================================
3. UI/UX DESIGN SYSTEM — APPLE LIQUID GLASS
=====================================================================
Visual Aesthetic:
- Full-screen scenic blurred mountain sunset landscape wallpaper (`sunset-wallpaper.jpg`) fixed in the background.
- Translucent frosted glass containers using:
  `background: rgba(255, 255, 255, 0.08) - rgba(15, 23, 42, 0.65)`
  `backdrop-filter: blur(24px) saturate(150%)`
  `-webkit-backdrop-filter: blur(24px) saturate(150%)`
  `border: 1px solid rgba(255, 255, 255, 0.16)`
  `box-shadow: 0 16px 40px rgba(0, 0, 0, 0.35)`
- Glowing luminous hover effects and smooth transitions (cubic-bezier 0.16, 1, 0.3, 1).
- Distinct color-coded status pills:
  - Applied: `#3b82f6` (blue glass)
  - Interview: `#f59e0b` (amber glass)
  - Selected: `#10b981` (emerald green glass)
  - Rejected: `#ef4444` (rose red glass)
  - Completed: `#8b5cf6` (purple glass)
  - Interested: `#06b6d4` (cyan glass)
  - Assessment: `#ec4899` (pink glass)
- Crisp SVG vector icons (graduation cap, search, bell, chevron, external link, edit, delete). Never use raw emojis that render as `?????`.

Application Layout Composition:
- Left Sidebar (Fixed 240px or ~19% width):
  - Brand header: Graduation cap badge + "Student Internship Tracker"
  - Navigation links: Dashboard, Internships, Add Internship, Profile, Settings
  - Active navigation pill: Translucent glowing blue indicator
  - Sidebar bottom: Logout link
- Center Shell:
  - Top bar with global search input, notification icon with unread badge, and user avatar profile pill (`AK` / initials).
  - Scrollable content viewport for dashboard metrics, tables, and form cards.

=====================================================================
4. CORE FUNCTIONAL MODULES
=====================================================================

Module 1: Authentication & Authorization
- Student Registration (`/register.html`):
  - Fields: Username, Email, Password, Full Name, Student ID.
  - Validation: Email uniqueness, strong password validation, username conflict prevention.
  - Passwords hashed using BCrypt before persistence.
- Student Login (`/login.html`):
  - Fields: Username/Email, Password.
  - Session-based authentication cookie or local credential token.
- Secure Logout with immediate redirect to login.

Module 2: Dashboard Metrics & Overview (`/dashboard.html`)
- Live Calculated Metric Cards:
  1. Total Applications (Count of all tracked internships)
  2. Applied (In review)
  3. Interviewing (Active interview rounds)
  4. Selected (Offers received)
- Recent Applications Table:
  - Columns: Company with 4-letter logo badge, Role, Type/Location pill, Status pill, Applied Date, Quick Edit action.
  - Dynamic empty state with call-to-action button when no internships exist.

Module 3: Internship Management & CRUD (`/internships.html`)
- Comprehensive List View:
  - Display all internships belonging to the logged-in student.
  - Columns: Company, Role, Location, Status, Applied Date, Actions (Edit, Delete).
- Real-time Client-side and Backend Filtering:
  - Live text search across company name and role.
  - Dropdown filter by status: All, Applied, Interview, Selected, Rejected, Completed.
- Delete Internship with interactive confirmation and immediate UI removal.

Module 4: Add & Edit Internship (`/add-internship.html`)
- Form Fields:
  - Company Name (required)
  - Role / Title (required)
  - Location (e.g. Bangalore, Remote, Hybrid)
  - Applied Date (date picker)
  - Status (Applied, Interview, Selected, Rejected, Completed)
  - Description & Interview Notes (textarea)
- Dual Behavior:
  - If URL has `?id=<ID>`, pre-populates fields, changes title to "Edit Internship", and sends `PUT` request.
  - Otherwise, sends `POST` request and displays "Internship added successfully!".
- Instant redirection to `/internships.html` upon save.

Module 5: Student Profile (`/profile.html`)
- Banner with student avatar SVG badge and header greeting.
- Editable Form Fields: Full Name, Student ID, Department, Year of Study (1st - 4th Year dropdown), Email Address, College/University, Skills & Expertise tags.
- Header Avatar and user badge dynamically update to match the saved student name.

Module 6: Settings & Preferences (`/settings.html`)
- Account details update.
- UI theme selection (Light / Dark glass).
- Notification checkboxes: Email Updates, Application Reminders.
- Password change modal/form with current password verification.

=====================================================================
5. DATABASE SCHEMA (SUPABASE POSTGRESQL 17)
=====================================================================
Execute the following schema in PostgreSQL:

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

=====================================================================
6. REST API CONTRACT
=====================================================================
- POST   /api/auth/register          -> Register student account
- POST   /api/auth/login             -> Authenticate & start session
- POST   /api/auth/logout            -> Terminate session
- GET    /api/auth/me                -> Get current authenticated student
- GET    /api/dashboard/stats        -> Calculate aggregate status metrics
- GET    /api/dashboard/recent       -> Retrieve 5 most recent internships
- GET    /api/internships            -> Filtered query (?search=&status=&type=)
- POST   /api/internships            -> Create new internship
- GET    /api/internships/{id}       -> Retrieve single internship details
- PUT    /api/internships/{id}       -> Update existing internship
- DELETE /api/internships/{id}       -> Delete internship
- GET    /api/profile                -> Retrieve student profile
- PUT    /api/profile                -> Update student profile
- GET    /api/settings               -> Retrieve student settings
- PUT    /api/settings/account       -> Update account settings
- PUT    /api/settings/preferences   -> Update theme & reminder flags
- PUT    /api/settings/password      -> Change password

=====================================================================
7. DUAL-MODE CLIENT-SIDE STORAGE ENGINE SPECIFICATION
=====================================================================
In `js/app.js`, wrap all `fetch()` calls inside an `apiRequest(endpoint, options)` wrapper:
- If running on Spring Boot (`response.ok`), return JSON directly.
- If response is 404 (static hosting on Vercel / Netlify) or network fails:
  Route the request to `handleClientFallback(endpoint, options)` backed by `ClientStore` in `localStorage`:
  - `GET /api/profile` and `PUT /api/profile` manipulate `sit_student`.
  - `/api/internships` CRUD manipulates `sit_internships`.
  - `/api/dashboard/stats` dynamically counts statuses from `sit_internships`.
- Support Clean URLs: Match routes using `path.includes('add-internship')` rather than strict `.endsWith('.html')` so Vercel URL stripping does not disable page initialization.

=====================================================================
8. DEPLOYMENT CONFIGURATIONS
=====================================================================
Netlify (`netlify.toml`):
[build]
  publish = "."
[[redirects]]
  from = "/"
  to = "/login.html"
  status = 302

Vercel (`vercel.json`):
{
  "cleanUrls": true,
  "rewrites": [
    { "source": "/", "destination": "/login.html" }
  ]
}

Spring Boot (`application.properties`):
spring.datasource.url=jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory
spring.datasource.username=postgres.dkrxjrszoovsurzurfbu
spring.datasource.password=8438391226Deva
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```
