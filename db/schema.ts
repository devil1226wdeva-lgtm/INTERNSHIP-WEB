import { pgTable, bigserial, bigint, varchar, text, boolean, date, timestamp, index } from "drizzle-orm/pg-core";

export const students = pgTable("students", {
  id: bigserial({ mode: "number" }).primaryKey(),
  username: varchar({ length: 50 }).notNull().unique(),
  email: varchar({ length: 100 }).notNull().unique(),
  password: varchar({ length: 255 }).notNull(),
  name: varchar("full_name", { length: 100 }),
  studentId: varchar("student_id", { length: 50 }),
  department: varchar({ length: 100 }),
  year: varchar("academic_year", { length: 30 }),
  college: varchar({ length: 150 }),
  skills: varchar({ length: 500 }),
  theme: varchar({ length: 20 }).default("light"),
  emailUpdates: boolean("email_updates").default(false),
  applicationReminders: boolean("application_reminders").default(false),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow(),
});

export const internships = pgTable(
  "internships",
  {
    id: bigserial({ mode: "number" }).primaryKey(),
    studentId: bigint("student_id", { mode: "number" })
      .notNull()
      .references(() => students.id, { onDelete: "cascade" }),
    company: varchar({ length: 100 }).notNull(),
    role: varchar({ length: 100 }).notNull(),
    location: varchar({ length: 100 }),
    appliedDate: date("applied_date"),
    status: varchar({ length: 30 }).notNull().default("APPLIED"),
    description: text(),
    internshipType: varchar("internship_type", { length: 50 }),
    startDate: date("start_date"),
    endDate: date("end_date"),
    websiteUrl: varchar("website_url", { length: 255 }),
    notes: text(),
    createdAt: timestamp("created_at").defaultNow().notNull(),
    updatedAt: timestamp("updated_at").defaultNow(),
  },
  (t) => [
    index("idx_internships_student").on(t.studentId),
    index("idx_internships_status").on(t.status),
    index("idx_internships_company").on(t.company),
  ]
);

export const sessions = pgTable("sessions", {
  token: varchar({ length: 128 }).primaryKey(),
  studentId: bigint("student_id", { mode: "number" })
    .notNull()
    .references(() => students.id, { onDelete: "cascade" }),
  expiresAt: timestamp("expires_at").notNull(),
});
