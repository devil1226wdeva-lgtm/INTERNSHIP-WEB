package com.student.internshiptracker.dto;

public class PreferencesRequest {

    private String theme;
    private Boolean emailUpdates;
    private Boolean applicationReminders;

    public PreferencesRequest() {
    }

    public PreferencesRequest(String theme, Boolean emailUpdates, Boolean applicationReminders) {
        this.theme = theme;
        this.emailUpdates = emailUpdates;
        this.applicationReminders = applicationReminders;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getEmailUpdates() {
        return emailUpdates;
    }

    public void setEmailUpdates(Boolean emailUpdates) {
        this.emailUpdates = emailUpdates;
    }

    public Boolean getApplicationReminders() {
        return applicationReminders;
    }

    public void setApplicationReminders(Boolean applicationReminders) {
        this.applicationReminders = applicationReminders;
    }
}
