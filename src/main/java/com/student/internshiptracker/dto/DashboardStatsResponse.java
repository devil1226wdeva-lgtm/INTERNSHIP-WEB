package com.student.internshiptracker.dto;

public class DashboardStatsResponse {

    private long total;
    private long applications; // applied
    private long interviews;   // interview
    private long selected;     // selected
    private long rejected;     // rejected
    private long completed;    // completed
    private long interested;
    private long assessment;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long total, long applications, long interviews, long selected, long rejected, long completed) {
        this.total = total;
        this.applications = applications;
        this.interviews = interviews;
        this.selected = selected;
        this.rejected = rejected;
        this.completed = completed;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getApplications() {
        return applications;
    }

    public void setApplications(long applications) {
        this.applications = applications;
    }

    public long getInterviews() {
        return interviews;
    }

    public void setInterviews(long interviews) {
        this.interviews = interviews;
    }

    public long getSelected() {
        return selected;
    }

    public void setSelected(long selected) {
        this.selected = selected;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getInterested() {
        return interested;
    }

    public void setInterested(long interested) {
        this.interested = interested;
    }

    public long getAssessment() {
        return assessment;
    }

    public void setAssessment(long assessment) {
        this.assessment = assessment;
    }
}
