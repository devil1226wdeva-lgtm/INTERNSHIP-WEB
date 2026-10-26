package com.student.internshiptracker.entity;

public enum ApplicationStatus {
    INTERESTED("Interested"),
    APPLIED("Applied"),
    ASSESSMENT("Assessment"),
    INTERVIEW("Interview"),
    SELECTED("Selected"),
    REJECTED("Rejected"),
    COMPLETED("Completed");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ApplicationStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return APPLIED;
        }
        for (ApplicationStatus s : ApplicationStatus.values()) {
            if (s.name().equalsIgnoreCase(status.trim()) || s.displayName.equalsIgnoreCase(status.trim())) {
                return s;
            }
        }
        return APPLIED;
    }
}
