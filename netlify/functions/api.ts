import type { Config } from "@netlify/functions";
import { randomBytes, scrypt, timingSafeEqual } from "node:crypto";
import { promisify } from "node:util";
import { and, count, desc, eq, gt, ilike, or, sql, type SQL } from "drizzle-orm";
import { db } from "../../db/index.js";
import { internships, sessions, students } from "../../db/schema.js";

// Port of the Spring Boot REST API (see src/main/java) so the app runs on Netlify.

type Student = typeof students.$inferSelect;
type Internship = typeof internships.$inferSelect;

const SESSION_COOKIE = "session";
const SESSION_TTL_MS = 7 * 24 * 60 * 60 * 1000;

const STATUSES: Record<string, string> = {
  INTERESTED: "Interested",
  APPLIED: "Applied",
  ASSESSMENT: "Assessment",
  INTERVIEW: "Interview",
  SELECTED: "Selected",
  REJECTED: "Rejected",
  COMPLETED: "Completed",
};

class HttpError extends Error {
  constructor(public status: number, message: string, public errors?: Record<string, string>) {
    super(message);
  }
}

// ---------- helpers ----------

const scryptAsync = promisify(scrypt) as (pw: string, salt: Buffer, len: number) => Promise<Buffer>;

async function hashPassword(password: string): Promise<string> {
  const salt = randomBytes(16);
  const key = await scryptAsync(password, salt, 64);
  return `scrypt$${salt.toString("hex")}$${key.toString("hex")}`;
}

async function verifyPassword(password: string, stored: string): Promise<boolean> {
  const [scheme, saltHex, keyHex] = stored.split("$");
  if (scheme !== "scrypt" || !saltHex || !keyHex) return false;
  const expected = Buffer.from(keyHex, "hex");
  const actual = await scryptAsync(password, Buffer.from(saltHex, "hex"), expected.length);
  return timingSafeEqual(expected, actual);
}

function statusFromString(value: unknown): string {
  const s = typeof value === "string" ? value.trim() : "";
  for (const [key, display] of Object.entries(STATUSES)) {
    if (key.toLowerCase() === s.toLowerCase() || display.toLowerCase() === s.toLowerCase()) return key;
  }
  return "APPLIED";
}

function str(value: unknown): string | null {
  if (value === undefined || value === null) return null;
  const s = String(value);
  return s === "" ? null : s;
}

function isBlank(value: unknown): boolean {
  return value === undefined || value === null || String(value).trim() === "";
}

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

type Rule = [field: string, check: (v: any) => boolean, message: string];

function validate(body: Record<string, any>, rules: Rule[]) {
  const errors: Record<string, string> = {};
  for (const [field, check, message] of rules) {
    if (!errors[field] && !check(body[field])) errors[field] = message;
  }
  if (Object.keys(errors).length > 0) {
    // Surface the first field message so the frontend toast is useful
    throw new HttpError(400, Object.values(errors)[0], errors);
  }
}

const required = (field: string, message: string): Rule => [field, (v) => !isBlank(v), message];
const length = (field: string, min: number, max: number, message: string): Rule => [
  field,
  (v) => isBlank(v) || (String(v).length >= min && String(v).length <= max),
  message,
];
const email = (field: string): Rule => [field, (v) => isBlank(v) || EMAIL_RE.test(String(v)), "Email must be valid"];

function ok(message: string, data?: unknown, status = 200, headers?: HeadersInit) {
  const body: Record<string, unknown> = { success: true, message };
  if (data !== undefined && data !== null) body.data = data;
  return Response.json(body, { status, headers });
}

function getCookie(req: Request, name: string): string | null {
  const header = req.headers.get("cookie") || "";
  for (const part of header.split(";")) {
    const [k, ...v] = part.trim().split("=");
    if (k === name) return decodeURIComponent(v.join("="));
  }
  return null;
}

function sessionCookie(token: string, maxAgeSeconds: number) {
  return `${SESSION_COOKIE}=${token}; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=${maxAgeSeconds}`;
}

async function readJson(req: Request): Promise<Record<string, any>> {
  try {
    const body = await req.json();
    return body && typeof body === "object" ? body : {};
  } catch {
    return {};
  }
}

function toAuthResponse(s: Student, message: string) {
  return {
    id: s.id,
    username: s.username,
    email: s.email,
    name: s.name,
    studentId: s.studentId,
    department: s.department,
    year: s.year,
    college: s.college,
    message,
  };
}

function toProfile(s: Student) {
  return {
    id: s.id,
    username: s.username,
    name: s.name,
    studentId: s.studentId,
    department: s.department,
    year: s.year,
    email: s.email,
    college: s.college,
    skills: s.skills,
  };
}

function toInternship(i: Internship) {
  return {
    id: i.id,
    company: i.company,
    role: i.role,
    location: i.location,
    appliedDate: i.appliedDate,
    status: STATUSES[i.status] ?? "Applied",
    description: i.description,
    internshipType: i.internshipType,
    startDate: i.startDate,
    endDate: i.endDate,
    websiteUrl: i.websiteUrl,
    notes: i.notes,
    createdAt: i.createdAt,
    updatedAt: i.updatedAt,
  };
}

function internshipValues(body: Record<string, any>) {
  return {
    company: String(body.company).trim(),
    role: String(body.role).trim(),
    location: str(body.location),
    appliedDate: str(body.appliedDate),
    status: statusFromString(body.status),
    description: str(body.description),
    internshipType: str(body.internshipType),
    startDate: str(body.startDate),
    endDate: str(body.endDate),
    websiteUrl: str(body.websiteUrl),
    notes: str(body.notes),
  };
}

const internshipRules: Rule[] = [
  required("company", "Company name is required"),
  required("role", "Role is required"),
  required("location", "Location is required"),
  required("appliedDate", "Applied date is required"),
  required("status", "Status is required"),
];

async function usernameTaken(username: string) {
  const rows = await db.select({ id: students.id }).from(students).where(sql`lower(${students.username}) = lower(${username})`);
  return rows.length > 0;
}

async function emailTaken(value: string) {
  const rows = await db.select({ id: students.id }).from(students).where(sql`lower(${students.email}) = lower(${value})`);
  return rows.length > 0;
}

async function currentStudent(req: Request): Promise<Student> {
  const token = getCookie(req, SESSION_COOKIE);
  if (!token) throw new HttpError(401, "User is not authenticated");
  const [row] = await db
    .select({ student: students })
    .from(sessions)
    .innerJoin(students, eq(sessions.studentId, students.id))
    .where(and(eq(sessions.token, token), gt(sessions.expiresAt, new Date())));
  if (!row) throw new HttpError(401, "User is not authenticated");
  return row.student;
}

async function ownedInternship(id: number, student: Student, action: string): Promise<Internship> {
  if (!Number.isInteger(id)) throw new HttpError(404, `Internship not found with ID: ${id}`);
  const [item] = await db.select().from(internships).where(eq(internships.id, id));
  if (!item) throw new HttpError(404, `Internship not found with ID: ${id}`);
  if (item.studentId !== student.id) throw new HttpError(403, `You are not authorized to ${action} this internship`);
  return item;
}

// ---------- route handlers ----------

async function handleAuth(req: Request, action: string): Promise<Response> {
  if (action === "register" && req.method === "POST") {
    const body = await readJson(req);
    validate(body, [
      required("username", "Username is required"),
      length("username", 3, 50, "Username must be between 3 and 50 characters"),
      required("email", "Email is required"),
      email("email"),
      required("password", "Password is required"),
      length("password", 6, Infinity, "Password must be at least 6 characters"),
    ]);
    const username = String(body.username).trim();
    const mail = String(body.email).trim();
    if (await usernameTaken(username)) throw new HttpError(409, `Username '${username}' is already taken`);
    if (await emailTaken(mail)) throw new HttpError(409, `Email '${mail}' is already registered`);

    const [saved] = await db
      .insert(students)
      .values({
        username,
        email: mail,
        password: await hashPassword(String(body.password)),
        name: isBlank(body.name) ? username : String(body.name).trim(),
        studentId: str(body.studentId),
        department: str(body.department),
        year: str(body.year),
        college: str(body.college),
      })
      .returning();
    return ok("Registration successful! You can now log in.", toAuthResponse(saved, "Registration successful"), 201);
  }

  if (action === "login" && req.method === "POST") {
    const body = await readJson(req);
    validate(body, [
      required("username", "Username or email is required"),
      required("password", "Password is required"),
    ]);
    const login = String(body.username).trim();
    const [student] = await db
      .select()
      .from(students)
      .where(or(sql`lower(${students.username}) = lower(${login})`, sql`lower(${students.email}) = lower(${login})`));
    if (!student || !(await verifyPassword(String(body.password), student.password))) {
      throw new HttpError(401, "Invalid username or password");
    }
    const token = randomBytes(32).toString("hex");
    await db.insert(sessions).values({ token, studentId: student.id, expiresAt: new Date(Date.now() + SESSION_TTL_MS) });
    return ok("Login successful", toAuthResponse(student, "Login successful"), 200, {
      "Set-Cookie": sessionCookie(token, SESSION_TTL_MS / 1000),
    });
  }

  if (action === "logout" && req.method === "POST") {
    const token = getCookie(req, SESSION_COOKIE);
    if (token) await db.delete(sessions).where(eq(sessions.token, token));
    return ok("Logged out successfully", undefined, 200, { "Set-Cookie": sessionCookie("", 0) });
  }

  if (action === "me" && req.method === "GET") {
    const student = await currentStudent(req);
    return ok("User authenticated", toAuthResponse(student, "Current student details retrieved"));
  }

  throw new HttpError(404, "Not found");
}

async function handleDashboard(req: Request, action: string): Promise<Response> {
  const student = await currentStudent(req);

  if (action === "stats" && req.method === "GET") {
    const rows = await db
      .select({ status: internships.status, n: count() })
      .from(internships)
      .where(eq(internships.studentId, student.id))
      .groupBy(internships.status);
    const by = (s: string) => rows.find((r) => r.status === s)?.n ?? 0;
    return ok("Dashboard statistics retrieved", {
      total: rows.reduce((sum, r) => sum + r.n, 0),
      applications: by("APPLIED"),
      interviews: by("INTERVIEW"),
      selected: by("SELECTED"),
      rejected: by("REJECTED"),
      completed: by("COMPLETED"),
      interested: by("INTERESTED"),
      assessment: by("ASSESSMENT"),
    });
  }

  if (action === "recent" && req.method === "GET") {
    const recent = await db
      .select()
      .from(internships)
      .where(eq(internships.studentId, student.id))
      .orderBy(sql`${internships.appliedDate} desc nulls last`, desc(internships.createdAt))
      .limit(5);
    return ok("Recent internships retrieved", recent.map(toInternship));
  }

  throw new HttpError(404, "Not found");
}

async function handleInternships(req: Request, rest: string[]): Promise<Response> {
  const student = await currentStudent(req);
  const [idPart, sub] = rest;

  if (!idPart) {
    if (req.method === "GET") {
      const params = new URL(req.url).searchParams;
      const conditions: (SQL | undefined)[] = [eq(internships.studentId, student.id)];
      const search = params.get("search")?.trim();
      const status = params.get("status")?.trim();
      const location = params.get("location")?.trim();
      const type = params.get("internshipType")?.trim();
      if (search) conditions.push(or(ilike(internships.company, `%${search}%`), ilike(internships.role, `%${search}%`)));
      if (status && status.toUpperCase() !== "ALL") conditions.push(eq(internships.status, statusFromString(status)));
      if (location) conditions.push(ilike(internships.location, `%${location}%`));
      if (type) conditions.push(ilike(internships.internshipType, `%${type}%`));

      const list = await db
        .select()
        .from(internships)
        .where(and(...conditions))
        .orderBy(sql`${internships.appliedDate} desc nulls last`, desc(internships.createdAt));
      return ok("Internships retrieved successfully", list.map(toInternship));
    }

    if (req.method === "POST") {
      const body = await readJson(req);
      validate(body, internshipRules);
      const [saved] = await db
        .insert(internships)
        .values({ ...internshipValues(body), studentId: student.id })
        .returning();
      return ok("Internship added successfully", toInternship(saved), 201);
    }

    throw new HttpError(405, "Method not allowed");
  }

  const id = Number(idPart);

  if (sub === "status" && req.method === "PATCH") {
    const body = await readJson(req);
    if (isBlank(body.status)) throw new HttpError(400, "Status cannot be empty");
    await ownedInternship(id, student, "update");
    const [updated] = await db
      .update(internships)
      .set({ status: statusFromString(body.status), updatedAt: new Date() })
      .where(eq(internships.id, id))
      .returning();
    return ok("Status updated successfully", toInternship(updated));
  }

  if (sub) throw new HttpError(404, "Not found");

  if (req.method === "GET") {
    const item = await ownedInternship(id, student, "view");
    return ok("Internship retrieved successfully", toInternship(item));
  }

  if (req.method === "PUT") {
    const body = await readJson(req);
    validate(body, internshipRules);
    await ownedInternship(id, student, "edit");
    const [updated] = await db
      .update(internships)
      .set({ ...internshipValues(body), updatedAt: new Date() })
      .where(eq(internships.id, id))
      .returning();
    return ok("Internship updated successfully", toInternship(updated));
  }

  if (req.method === "DELETE") {
    await ownedInternship(id, student, "delete");
    await db.delete(internships).where(eq(internships.id, id));
    return ok("Internship deleted successfully");
  }

  throw new HttpError(405, "Method not allowed");
}

async function handleProfile(req: Request): Promise<Response> {
  const student = await currentStudent(req);

  if (req.method === "GET") return ok("Profile retrieved successfully", toProfile(student));

  if (req.method === "PUT") {
    const body = await readJson(req);
    validate(body, [
      required("name", "Full name is required"),
      required("studentId", "Student ID is required"),
      required("department", "Department is required"),
      required("year", "Year is required"),
      required("email", "Email is required"),
      email("email"),
      required("college", "College is required"),
    ]);
    const mail = String(body.email).trim();
    if (mail.toLowerCase() !== student.email.toLowerCase() && (await emailTaken(mail))) {
      throw new HttpError(409, `Email '${mail}' is already in use by another account`);
    }
    const [updated] = await db
      .update(students)
      .set({
        email: mail,
        name: String(body.name).trim(),
        studentId: String(body.studentId).trim(),
        department: String(body.department).trim(),
        year: String(body.year).trim(),
        college: String(body.college).trim(),
        skills: str(body.skills),
        updatedAt: new Date(),
      })
      .where(eq(students.id, student.id))
      .returning();
    return ok("Profile updated successfully", toProfile(updated));
  }

  throw new HttpError(405, "Method not allowed");
}

async function handleSettings(req: Request, action: string | undefined): Promise<Response> {
  const student = await currentStudent(req);

  if (!action && req.method === "GET") {
    return ok("Settings retrieved successfully", {
      username: student.username,
      email: student.email,
      theme: student.theme ?? "light",
      emailUpdates: !!student.emailUpdates,
      applicationReminders: !!student.applicationReminders,
    });
  }

  if (action === "account" && req.method === "PUT") {
    const body = await readJson(req);
    validate(body, [
      required("username", "Username cannot be empty"),
      length("username", 3, 50, "Username must be between 3 and 50 characters"),
      required("email", "Email cannot be empty"),
      email("email"),
    ]);
    const username = String(body.username).trim();
    const mail = String(body.email).trim();
    if (username.toLowerCase() !== student.username.toLowerCase() && (await usernameTaken(username))) {
      throw new HttpError(409, `Username '${username}' is already taken`);
    }
    if (mail.toLowerCase() !== student.email.toLowerCase() && (await emailTaken(mail))) {
      throw new HttpError(409, `Email '${mail}' is already in use`);
    }
    await db.update(students).set({ username, email: mail, updatedAt: new Date() }).where(eq(students.id, student.id));
    return ok("Account settings updated successfully");
  }

  if (action === "password" && req.method === "PUT") {
    const body = await readJson(req);
    validate(body, [
      required("currentPassword", "Current password is required"),
      required("newPassword", "New password is required"),
      length("newPassword", 6, Infinity, "New password must be at least 6 characters"),
      required("confirmPassword", "Confirm password is required"),
    ]);
    if (!(await verifyPassword(body.currentPassword, student.password))) {
      throw new HttpError(400, "Current password does not match");
    }
    if (body.newPassword !== body.confirmPassword) {
      throw new HttpError(400, "New password and confirmation password do not match");
    }
    if (await verifyPassword(body.newPassword, student.password)) {
      throw new HttpError(400, "New password cannot be the same as your old password");
    }
    await db
      .update(students)
      .set({ password: await hashPassword(body.newPassword), updatedAt: new Date() })
      .where(eq(students.id, student.id));
    return ok("Password changed successfully");
  }

  if (action === "preferences" && req.method === "PUT") {
    const body = await readJson(req);
    const changes: Partial<Student> = { updatedAt: new Date() };
    if (!isBlank(body.theme)) changes.theme = String(body.theme).toLowerCase();
    if (typeof body.emailUpdates === "boolean") changes.emailUpdates = body.emailUpdates;
    if (typeof body.applicationReminders === "boolean") changes.applicationReminders = body.applicationReminders;
    await db.update(students).set(changes).where(eq(students.id, student.id));
    return ok("Preferences updated successfully");
  }

  throw new HttpError(404, "Not found");
}

// ---------- entry point ----------

export default async (req: Request) => {
  const [, , resource, ...rest] = new URL(req.url).pathname.replace(/\/+$/, "").split("/");

  try {
    switch (resource) {
      case "auth":
        return await handleAuth(req, rest[0] ?? "");
      case "dashboard":
        return await handleDashboard(req, rest[0] ?? "");
      case "internships":
        return await handleInternships(req, rest);
      case "profile":
        return await handleProfile(req);
      case "settings":
        return await handleSettings(req, rest[0]);
      default:
        throw new HttpError(404, "Not found");
    }
  } catch (err) {
    if (err instanceof HttpError) {
      return Response.json(
        { status: err.status, message: err.message, ...(err.errors ? { errors: err.errors } : {}) },
        { status: err.status }
      );
    }
    console.error(err);
    return Response.json(
      { status: 500, message: "An unexpected error occurred. Please try again later." },
      { status: 500 }
    );
  }
};

export const config: Config = {
  path: "/api/*",
};
