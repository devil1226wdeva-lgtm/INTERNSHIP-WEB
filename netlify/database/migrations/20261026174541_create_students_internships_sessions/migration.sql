CREATE TABLE "internships" (
	"id" bigserial PRIMARY KEY,
	"student_id" bigint NOT NULL,
	"company" varchar(100) NOT NULL,
	"role" varchar(100) NOT NULL,
	"location" varchar(100),
	"applied_date" date,
	"status" varchar(30) DEFAULT 'APPLIED' NOT NULL,
	"description" text,
	"internship_type" varchar(50),
	"start_date" date,
	"end_date" date,
	"website_url" varchar(255),
	"notes" text,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now()
);
--> statement-breakpoint
CREATE TABLE "sessions" (
	"token" varchar(128) PRIMARY KEY,
	"student_id" bigint NOT NULL,
	"expires_at" timestamp NOT NULL
);
--> statement-breakpoint
CREATE TABLE "students" (
	"id" bigserial PRIMARY KEY,
	"username" varchar(50) NOT NULL UNIQUE,
	"email" varchar(100) NOT NULL UNIQUE,
	"password" varchar(255) NOT NULL,
	"full_name" varchar(100),
	"student_id" varchar(50),
	"department" varchar(100),
	"academic_year" varchar(30),
	"college" varchar(150),
	"skills" varchar(500),
	"theme" varchar(20) DEFAULT 'light',
	"email_updates" boolean DEFAULT false,
	"application_reminders" boolean DEFAULT false,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now()
);
--> statement-breakpoint
CREATE INDEX "idx_internships_student" ON "internships" ("student_id");--> statement-breakpoint
CREATE INDEX "idx_internships_status" ON "internships" ("status");--> statement-breakpoint
CREATE INDEX "idx_internships_company" ON "internships" ("company");--> statement-breakpoint
ALTER TABLE "internships" ADD CONSTRAINT "internships_student_id_students_id_fkey" FOREIGN KEY ("student_id") REFERENCES "students"("id") ON DELETE CASCADE;--> statement-breakpoint
ALTER TABLE "sessions" ADD CONSTRAINT "sessions_student_id_students_id_fkey" FOREIGN KEY ("student_id") REFERENCES "students"("id") ON DELETE CASCADE;